package jsonparsing;

import lombok.Data;
import pojo.StationPojo;

import java.util.ArrayList;

@Data
public class StationFileModel {
    String status;
    ArrayList<StationPojo> list;
}
