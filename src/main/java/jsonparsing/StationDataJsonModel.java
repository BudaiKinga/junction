package jsonparsing;

import lombok.Getter;
import lombok.Setter;
import pojo.Station;

import java.util.ArrayList;

@Getter
@Setter
class StationDataJsonModel {
    String status;
    ArrayList<Station> list;
}
