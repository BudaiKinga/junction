package jsonparsing;

import com.google.gson.Gson;
import pojo.Heartbeat;
import pojo.Station;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JsonParser {

    private static final Gson GSON = new Gson();

    public static List<Station> getStations() throws Exception {
        FileReader jsonFileReader = new FileReader("src\\main\\resources\\station.json");
        StationDataJsonModel stationDataJsonModel = GSON.fromJson(jsonFileReader, StationDataJsonModel.class);
        return stationDataJsonModel.getList();
    }

    private static List<Heartbeat> getHeartBeats(String json) {
        if (json == null) {
            return Collections.emptyList();
        }
        RawDataJsonModel rawDataJsonModel = GSON.fromJson(json, RawDataJsonModel.class);
        return rawDataJsonModel.getRaw() == null ? Collections.emptyList() : rawDataJsonModel.getRaw();
    }

    public static List<Heartbeat> getHeartBeats(List<String> jsons) {
        long start = System.currentTimeMillis();

        List<Heartbeat> result = new ArrayList<>();
        for (String json : jsons) {
            result.addAll(getHeartBeats(json));
        }

        long end = System.currentTimeMillis();
        System.out.println("Parsing heartbeats took: " + ((end - start) / 1_000));

        return result;

    }
}
