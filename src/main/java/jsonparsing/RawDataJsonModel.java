package jsonparsing;

import lombok.Getter;
import lombok.Setter;
import pojo.Heartbeat;

import java.util.ArrayList;

@Getter
@Setter
class RawDataJsonModel {
    private String status;
    ArrayList<Heartbeat> raw;
}
