import jsonparsing.JsonParser;
import model.Observation;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pojo.Heartbeat;
import pojo.Station;
import processor.ParallelRequestExecutor;
import request.RawRequest;
import request.Request;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static utils.CsvWriter.writeTocsv;

public class Benchmark {

    private static final int JUMP_MINUTES = 30;
    private static final String START_TIME_STRING = "2019-08-01 08:00:00";
    private static final String END_TIME_STRING = "2019-08-01 12:00:00";
    private static final int PEEK_MINUTES = 1;

    private static ParallelRequestExecutor executor = new ParallelRequestExecutor(100);

    public static void main(String[] args) throws Exception {
        checkResponseGrowthParallel();
        checkResponseGrowth();
        buildStatistics();
    }

    private static void checkResponseGrowthParallel() throws IOException, InterruptedException {
        File csv = new File("responseGrowth_parallel.csv");
        BufferedWriter wr = new BufferedWriter(new FileWriter(csv));
        wr.write("peek, time, total, distinct\n");

        // create pool of 100
        ExecutorService pool = Executors.newFixedThreadPool(100);

        for (int peekSeconds = 1; peekSeconds < 30; peekSeconds++) {
            long start = System.currentTimeMillis();

            List<Callable<String>> callables = new ArrayList<>();


            for (int i = 0; i < peekSeconds; i++) {
                Request r = new RawRequest();
                r.setCommand("list");
                long time = Timestamp.valueOf("2019-08-01 08:00:00").getTime() + (i * 20_000);
                long timeE = Timestamp.valueOf("2019-08-01 08:00:00").getTime() + ((i + 1) * 20_000);
                r.setTimeStart(new Date(time));
                r.setTimeStop(new Date(timeE));

                callables.add(() -> {
                    HttpPost post = r.postRequest();
                    CloseableHttpClient client = HttpClients.createDefault();
                    HttpResponse response = client.execute(post);
                    return EntityUtils.toString(response.getEntity());
                });
            }

            List<Future<String>> results = pool.invokeAll(callables);

            List<String> heartbeatsString = results.stream().map(res -> {
                try {
                    return res.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println("Out of time...");
                return null;
            }).collect(Collectors.toList());

            List<Heartbeat> heartbeats = JsonParser.getHeartBeats(heartbeatsString);


            long end = System.currentTimeMillis();

            long distinct = heartbeats.stream().map(Heartbeat::getHash).distinct().count();
            long elapsed = (end - start);
            System.out.println("peek: " + peekSeconds +
                    ", time: " + elapsed +
                    ", nrResponses: " + heartbeats.size() +
                    ", distinct: " + distinct);
            wr.write(peekSeconds + ", " + elapsed + ", " + heartbeats.size() + ", " + distinct + "\n");
        }
        wr.close();
        pool.shutdown();

    }

    private static void checkResponseGrowth() throws IOException {
        File csv = new File("responseGrowth.csv");
        BufferedWriter wr = new BufferedWriter(new FileWriter(csv));
        wr.write("peek, time, total, distinct\n");

        for (int peekSeconds = 1; peekSeconds < 30; peekSeconds++) {
            long start = System.currentTimeMillis();
            Request r = new RawRequest();
            r.setCommand("list");
            long time = Timestamp.valueOf("2019-08-01 08:00:00").getTime();
            r.setTimeStart(new Date(time));
            r.setTimeStop(new Date(time + peekSeconds * 1000));

            HttpPost post = r.postRequest();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(post);

            String jsonString = EntityUtils.toString(response.getEntity());
            List<Heartbeat> heartbeats = JsonParser.getHeartBeats(jsonString);
            long end = System.currentTimeMillis();

            long distinct = heartbeats.stream().map(Heartbeat::getHash).distinct().count();
            long elapsed = (end - start) / 1000;
            System.out.println("peek: " + peekSeconds +
                    ", time: " + elapsed +
                    ", nrResponses: " + heartbeats.size() +
                    ", distinct: " + distinct);
            wr.write(peekSeconds + ", " + elapsed + ", " + heartbeats.size() + ", " + distinct + "\n");
        }
        wr.close();
    }

    private static void buildStatistics() throws Exception {
        long startTime = System.currentTimeMillis();

        Map<String, String> stationsSerialToDescription = getStationSerialToDescriptionMap();

        List<Request> requests = buildCoarseGrainRequestsByMinute(START_TIME_STRING, END_TIME_STRING, JUMP_MINUTES, PEEK_MINUTES);

        List<String> jsonResponses = executor.execute(requests);

        List<Heartbeat> heartbeats = JsonParser.getHeartBeats(jsonResponses);

        Set<Observation> observations = getObservations(heartbeats, JUMP_MINUTES, stationsSerialToDescription);

        writeTocsv(observations);

        long endTime = System.currentTimeMillis();
        System.out.println("time: " + (endTime - startTime));
    }

    public static List<Request> buildCoarseGrainRequestsByMinute(String startTime, String endTime, int jump, int peek) {
        return buildCoarseGrainRequestsBySecond(startTime, endTime, jump * 60, peek * 60);
    }

    public static List<Request> buildCoarseGrainRequestsBySecond(String startTime, String endTime, int jump, int peek) {
        List<Request> result = new ArrayList<>();
        Date startDate = new Date(Timestamp.valueOf(startTime).getTime());
        Date endDate = new Date(Timestamp.valueOf(endTime).getTime());

        for (Date d = startDate; d.before(endDate); d = DateUtils.addSeconds(d, jump)) {
            Request coarseRawRequest = new RawRequest();
            coarseRawRequest.setCommand("list");
            coarseRawRequest.setTimeStart(d);
            coarseRawRequest.setTimeStop(DateUtils.addSeconds(d, peek));
            result.add(coarseRawRequest);
        }

        return result;
    }

    public static Map<String, String> getStationSerialToDescriptionMap() throws Exception {
        Map<String, String> stations = new HashMap<>();
        for (Station sp : JsonParser.getStations()) {
            stations.put(sp.getSerial(), sp.getDescription());
        }
        return stations;
    }

    public static Set<Observation> getObservations(List<Heartbeat> heartbeats, int jump, Map<String, String> stationPojos) {
        Set<Observation> obs = new HashSet<>();
        for (Heartbeat pojo : heartbeats) {
            obs.add(convertToObs(pojo, jump, stationPojos));
        }
        return obs;
    }

    private static Observation convertToObs(Heartbeat pojo, int jump, Map<String, String> stationPojos) {
        Observation observation = new Observation();
        observation.setRawHash(pojo.getHash());
        observation.setLocation(stationPojos.get(pojo.getSerial()));
        Date pojoTime = new Date(pojo.getTime().getTime());
        pojoTime.setSeconds(pojoTime.getSeconds() - (pojoTime.getSeconds() % jump));
        observation.setStartDate(pojoTime);
        return observation;
    }

    private static void topTenVisited(List<Station> stations, Map<String, Set<String>> checkInCounter) {
        int nrMax = 25;
        for (int i = 0; i < nrMax; i++) {
            int max = 0;
            Station sMax = null;
            for (Station s : stations) {
                Set<String> r = checkInCounter.get(s.getSerial());
                if (r != null && r.size() > max) {
                    sMax = s;
                    max = r.size();
                }
            }
            System.out.println("Max" + i + ": " + sMax.getDescription() + " " + max);
            checkInCounter.put(sMax.getSerial(), new HashSet<>());
        }
    }

    private static Map<String, Set<String>> countCheckIns(List<Heartbeat> heartbeats) {
        Map<String, Set<String>> checkInCounter = new HashMap<>();

        for (Heartbeat raw : heartbeats) {
            Set<String> hashes = checkInCounter.get(raw.getSerial());
            if (hashes == null) {
                hashes = new HashSet<>();
            }
            hashes.add(raw.getHash());
            checkInCounter.put(raw.getSerial(), hashes);
        }
        return checkInCounter;
    }
}
