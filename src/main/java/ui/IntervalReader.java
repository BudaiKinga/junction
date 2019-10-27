package ui;

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
