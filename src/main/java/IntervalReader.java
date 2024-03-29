import jsonparsing.JsonParser;
import model.Observation;
import pojo.Heartbeat;
import processor.ParallelRequestExecutor;
import request.Request;
import utils.IOUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static processor.ParallelRequestExecutor.STOP;

public class IntervalReader {

    private static final ParallelRequestExecutor executor = new ParallelRequestExecutor(100);

    public static void main(String[] args) throws Exception {
        // 10s peek whole day coverage
        String START_TIME_STRING = "2019-08-01 08:00:00";
        String END_TIME_STRING = "2019-08-01 11:30:00";
        Set<Observation> observations = getRawDataBetweenInterval(START_TIME_STRING, END_TIME_STRING);
        IOUtil.writeTocsv(observations, START_TIME_STRING);
    }

    public static Set<Observation> getRawDataBetweenInterval(String startTime, String endTime) throws InterruptedException, IOException, ParseException {
        List<Request> requests = Benchmark.buildCoarseGrainRequestsBySecond(startTime, endTime, 10, 10);

        BlockingQueue<String> retrievedJsons = new LinkedBlockingQueue<>();

        new Thread(() -> {
            try {
                executor.execute(requests, retrievedJsons);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        final Set<Observation> observations = new HashSet<>();
        Thread parser = new Thread(() -> {
            while (true) {
                try {
                    String json = retrievedJsons.take();
                    if (STOP.equals(json)) {
                        break;
                    }
                    List<Heartbeat> heartBeats = JsonParser.getHeartBeats(json);
                    observations.addAll(Benchmark.getObservations(heartBeats, 10, Benchmark.getStationSerialToDescriptionMap()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        parser.start();
        parser.join();
        return observations;
    }
}
