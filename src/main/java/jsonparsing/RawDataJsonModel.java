package jsonparsing;

import lombok.Getter;
import lombok.Setter;
import pojo.RawPojo;

import java.util.ArrayList;

@Getter
@Setter
class RawDataJsonModel {
    private String status;
    ArrayList<RawPojo> raw;
}
