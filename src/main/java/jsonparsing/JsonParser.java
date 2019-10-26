package jsonparsing;

import com.google.gson.Gson;
import pojo.RawPojo;
import pojo.StationPojo;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JsonParser {

    private static final Gson GSON = new Gson();

    public static List<StationPojo> getStations() throws Exception {
        FileReader jsonFileReader = new FileReader("src\\main\\resources\\station.json");
        StationDataJsonModel stationDataJsonModel = GSON.fromJson(jsonFileReader, StationDataJsonModel.class);
        return stationDataJsonModel.getList();
    }

    public static List<RawPojo> getRaw(String json) {
        RawDataJsonModel rawDataJsonModel = GSON.fromJson(json, RawDataJsonModel.class);
        return rawDataJsonModel.getRaw();
    }

    public static List<RawPojo> getRaw(List<String> jsons) {
        List<RawPojo> result = new ArrayList<>();
        for (String json: jsons) {
            result.addAll(getRaw(json));
        }
        return result;
    }
}
