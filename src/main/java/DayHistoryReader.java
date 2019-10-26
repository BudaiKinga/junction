import jsonparsing.JsonParser;
import model.Observation;
import pojo.Heartbeat;
import processor.ParallelRequestExecutor;
import request.Request;
import utils.IOUtil;

import java.util.List;
import java.util.Set;

public class DayHistoryReader {

    private static final ParallelRequestExecutor executor = new ParallelRequestExecutor(100);

    public static void main(String[] args) throws Exception {
        // 10s peek whole day coverage
        String START_TIME_STRING = "2019-08-01 08:00:00";
        String END_TIME_STRING = "2019-08-01 08:00:20";
        List<Request> requests = Benchmark.buildCoarseGrainRequestsBySecond(START_TIME_STRING, END_TIME_STRING, 10, 10);

        List<String> responses = executor.execute(requests);

        List<Heartbeat> heartBeats = JsonParser.getHeartBeats(responses);
        Set<Observation> observations = Benchmark.getObservations(heartBeats, 10, Benchmark.getStationSerialToDescriptionMap());

        IOUtil.writeTocsv(observations);
    }
}
