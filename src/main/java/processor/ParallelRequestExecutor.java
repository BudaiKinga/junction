package processor;

import jsonparsing.JsonParser;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pojo.RawPojo;
import pojo.StationPojo;
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

public class ParallelRequestExecutor {

    private final ExecutorService pool;

    public ParallelRequestExecutor(int poolSize) {
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public List<String> execute(List<Request> requests) throws InterruptedException {
        List<Request> smallWinReq = getSmallWindowRequests(requests);

        List<Callable<String>> callables = new ArrayList<>();
        for (Request request : smallWinReq)
            callables.add(createCallable(request));

        List<Future<String>> results = pool.invokeAll(callables);
        pool.shutdown();

        return results.stream().map(res -> {
            try {
                return res.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Out of time...");
            return null;
        }).collect(Collectors.toList());
    }

    private List<Request> getSmallWindowRequests(List<Request> requests) {
        List<Request> res = new ArrayList<>();
        for (Request r : requests) {
            List<Request> fineGrain = getFineGrain(r);
            res.addAll(fineGrain);
        }
        return res;
    }

    private List<Request> getFineGrain(Request r) {
        long start = r.getTimeStart().getTime();
        long end = r.getTimeStop().getTime();
        long iteration = (end - start) / 20_000;
        List<Request> list = new ArrayList<>();
        for (int i = 0; i < iteration; i++) {
            Request rSmall = new RawRequest();
            rSmall.setCommand(r.getCommand());
            rSmall.setTimeStart(new Date(start + i * 20 * 1000));
            rSmall.setTimeStop(new Date(start + (i * 20000 + 1000)));
            list.add(rSmall);
        }
        return list;
    }

    private Callable<String> createCallable(Request request) {
        return () -> {
            HttpPost post = request.postRequest();

            CloseableHttpClient client = HttpClients.createDefault();

            HttpResponse response = client.execute(post);
            return EntityUtils.toString(response.getEntity());
        };
    }

    private static List<Request> buildCoarseGrainRequests(String startTime, String endTime, int jump, int peek) {
        List<Request> result = new ArrayList<>();
        Date startDate = new Date(Timestamp.valueOf(startTime).getTime());
        Date endDate = new Date(Timestamp.valueOf(endTime).getTime());

        for (Date d = startDate; d.before(endDate); DateUtils.addMinutes(d, jump)) {
            Request coarseRawRequest = new RawRequest();
            coarseRawRequest.setCommand("list");
            coarseRawRequest.setTimeStart(d);
            coarseRawRequest.setTimeStop(DateUtils.addMinutes(d, peek));
            result.add(coarseRawRequest);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        List<StationPojo> stations = JsonParser.getStations();

        List<Request> requests = buildCoarseGrainRequests("2019-08-01 08:00:00", "2019-08-02 08:00:00", 30, 3);

        ParallelRequestExecutor exec = new ParallelRequestExecutor(100);

        List<String> jsonResponses = exec.execute(requests);

        List<RawPojo> rawPojos = JsonParser.getRaw(jsonResponses);


        //Map<String, Set<String>> checkInCounter = countCheckIns(rawPojos);

        //topTenVisited(stations, checkInCounter);
       // writeTocsv(checkInCounter, stations);

        long endTime = System.currentTimeMillis();
        System.out.println("time: " + (endTime - startTime));

    }

    private static void writeTocsv(Map<String, Set<String>> checkInCounter, Set<StationPojo> stationPojos) throws IOException {
        File csv = new File("data1.csv");
        BufferedWriter wr = new BufferedWriter(new FileWriter(csv));
        wr.write("description, time, nrVisitors\n");


    }

    private static void topTenVisited(List<StationPojo> stations, Map<String, Set<String>> checkInCounter) {
        int nrMax = 25;
        for (int i = 0; i < nrMax; i++) {
            int max = 0;
            StationPojo sMax = null;
            for (StationPojo s : stations) {
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

    private static Map<String, Set<String>> countCheckIns(List<RawPojo> rawPojos) {
        Map<String, Set<String>> checkInCounter = new HashMap<>();

        for (RawPojo raw : rawPojos) {
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
