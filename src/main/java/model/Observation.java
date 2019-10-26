package model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
public class Observation {

    private Date startDate;
    private String location;
    private String rawHash;

    @Override
    public String toString() {
        return startDate + "," + rawHash + "," + location;
    }

    public static Observation fromString(String line) throws ParseException {
        Observation o = new Observation();
        String[] tokens = line.split(",");
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        System.out.println(dateFormat.parse(tokens[0]));
        o.setStartDate(dateFormat.parse(tokens[0]));
        o.setRawHash(tokens[1]);
        o.setLocation(tokens[2]);
        return o;
    }
}
