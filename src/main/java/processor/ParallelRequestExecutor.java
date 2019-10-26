package processor;

import jsonparsing.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pojo.RawPojo;
import pojo.StationPojo;
import request.RawRequest;
import request.Request;

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

        List<String> responses = results.stream().map(res -> {
            try {
                return res.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Out of time...");
            return null;
        }).collect(Collectors.toList());
        return responses;
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
            rSmall.setTimeStart(new Date(start + i*20*1000));
            rSmall.setTimeStop(new Date(start + (i+1)*20000 -1000));
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

    public static void main(String[] args) throws Exception {
        List<StationPojo> stations = JsonParser.getStations();
        Request raw1 = new RawRequest();
        raw1.setCommand("list");
        raw1.setTimeStart(new Date(Timestamp.valueOf("2019-08-01 12:00:00").getTime()));
        raw1.setTimeStop(new Date(Timestamp.valueOf("2019-08-01 12:01:00").getTime()));
        List<Request> requests = new ArrayList<>(Arrays.asList(
                raw1
        ));


        ParallelRequestExecutor exec = new ParallelRequestExecutor(3);

        List<String> res = exec.execute(requests);

        // refactor -> move to jsonParser
        List<RawPojo> raws = new ArrayList<>();
        for (String r : res) {
            raws.addAll(JsonParser.getRaw(r));
        }

        Map<String, Integer> checkInCounter = new HashMap<>();

        for (RawPojo raw : raws) {
            Integer counter = checkInCounter.get(raw.getSerial());
            if (counter == null) {
                counter = 1;
            } else {
                counter++;
            }
            checkInCounter.put(raw.getSerial(), counter);
        }
        // top ten
        int nrMax = 10;
        for (int i = 0; i < nrMax; i++) {
            int max = 0;
            StationPojo sMax = null;
            for (StationPojo s : stations) {
                System.out.println(s.getDescription());
                int count = checkInCounter.get(s.getSerial());
                if (count > max) {
                    sMax = s;
                    max = count;
                }
            }
            System.out.println("Max" + i + ": " + sMax.getDescription() + " " + max);
            checkInCounter.put(sMax.getDescription(), 0);
        }

    }
}
