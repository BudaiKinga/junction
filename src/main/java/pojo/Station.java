package pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Station {
    private String serial;
    private String address;
    private String postalcode;
    private String city;
    private String country;
    private String description;
    private float latitude;
    private float longitude;
    private String group;
    private String alias;


}
