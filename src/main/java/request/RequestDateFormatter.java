package request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RequestDateFormatter {
    public static String getFormattedDate(Date date) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String text = sdf.format(date);
        return text;
    }

    public static String getFormattedDateForFile(String dateStr) throws ParseException {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sdf.parse(dateStr);
        sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return sdf.format(d);
    }
}
