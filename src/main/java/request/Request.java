package request;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.methods.HttpPost;

import java.util.Date;

@Getter
@Setter
public abstract class Request {

    static final String BASIC_URL = "https://api.hypr.cl/";

    private static final String API_KEY = "iQ0WKQlv3a7VqVSKG6BlE9IQ88bUYQws6UZLRs1B";
    private String command;
    private Date timeStart;
    private Date timeStop;

    public HttpPost postRequest() {
        HttpPost httpPost = new HttpPost(getBaseUrl());
        httpPost.addHeader("x-api-key", API_KEY);
        httpPost.addHeader("command", command);
        httpPost.addHeader("time_start", RequestDateFormatter.getFormattedDate(timeStart));
        httpPost.addHeader("time_stop", RequestDateFormatter.getFormattedDate(timeStop));

        return httpPost;
    }

    public abstract String getBaseUrl();

}
