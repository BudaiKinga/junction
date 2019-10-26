package jsonparsing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pojo.Heartbeat;
import pojo.Station;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class JsonParser {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").create();

    public static List<Station> getStations() throws Exception {
        FileReader jsonFileReader = new FileReader("src\\main\\resources\\station.json");
        StationDataJsonModel stationDataJsonModel = GSON.fromJson(jsonFileReader, StationDataJsonModel.class);
        return stationDataJsonModel.getList();
    }

    public static List<Heartbeat> getHeartBeats(String json) {
        if (json == null) {
            return Collections.emptyList();
        }
        RawDataJsonModel rawDataJsonModel = GSON.fromJson(json, RawDataJsonModel.class);
        return rawDataJsonModel.getRaw() == null ? Collections.emptyList() : rawDataJsonModel.getRaw();
    }

    public static List<Heartbeat> getHeartBeats(List<String> jsons) {
        long start = System.currentTimeMillis();
        System.out.println("Parsing " + jsons.size() + " jsons");

        List<Heartbeat> result = new ArrayList<>();
        Iterator<String> iterator = jsons.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String json = iterator.next();
            result.addAll(getHeartBeats(json));
            System.out.println("json " + (i++) + " processed");
            iterator.remove();
        }

        long end = System.currentTimeMillis();
        System.out.println("Parsing heartbeats took: " + ((end - start) / 1_000));

        return result;

    }
}
