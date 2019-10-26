package pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Heartbeat {
    private Date time;
    private String serial;
    private float latitude;
    private float longitude;
    private String hash;
    private float distance;
    private float rssi;
}
