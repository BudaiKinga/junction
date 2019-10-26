package jsonparsing;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import pojo.StationPojo;

import java.io.FileReader;
import java.util.List;

public final class JsonParser {

    public static List<StationPojo> getStations() throws Exception {
        Gson gson = new Gson();
        FileReader jsonFileReader = new FileReader("..\\station_json.json");
        JsonReader reader = new JsonReader(jsonFileReader);
        StationFileModel stationFileModel = gson.fromJson(jsonFileReader, StationFileModel.class);
        return stationFileModel.getList();
    }
}
