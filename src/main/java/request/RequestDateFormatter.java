package request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RequestDateFormatter {
    public static String getFormattedDate(Date date) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        String text = sdf.format(date);
        return text;
    }
}
