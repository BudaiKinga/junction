package jsonparsing;

import com.google.gson.Gson;
import pojo.RawPojo;
import pojo.StationPojo;

import java.io.FileReader;
import java.util.List;

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
}
