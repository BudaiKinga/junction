package request;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.methods.HttpPost;

import java.util.Date;

@Getter
@Setter
public class Request {

    private static final String BASIC_URL = "https://api.hypr.cl/station";

    private static final String API_KEY = "iQ0WKQlv3a7VqVSKG6BlE9IQ88bUYQws6UZLRs1B";
    private String command;
    private Date timeStart;
    private Date timeStop;

    public HttpPost postRequest() {
        HttpPost httpPost = new HttpPost(BASIC_URL);
        httpPost.addHeader("x-api-key", API_KEY);
        httpPost.addHeader("command", command);
        httpPost.addHeader("timeStart", RequestDateFormatter.getFormattedDate(timeStart));
        httpPost.addHeader("timeStop", RequestDateFormatter.getFormattedDate(timeStop));

        return httpPost;
    }

}
