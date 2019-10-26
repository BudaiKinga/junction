package jsonparsing;

import lombok.Getter;
import lombok.Setter;
import pojo.StationPojo;

import java.util.ArrayList;

@Getter
@Setter
class StationDataJsonModel {
    String status;
    ArrayList<StationPojo> list;
}
