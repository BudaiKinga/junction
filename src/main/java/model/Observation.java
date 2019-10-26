package model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
public class Observation {

    public Date startDate;
    public String location;
    public String rawHash;

    @Override
    public String toString() {
        return startDate + "," + rawHash + "," + location;
    }
}
