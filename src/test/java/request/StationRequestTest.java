package request;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class StationRequestTest {
    @org.junit.Test
    public void postRequest() throws Exception {
        StationRequest r = new StationRequest();
        r.setCommand("list");
        r.setTimeStart(new Date());
        r.setTimeStop(new Date());

        HttpPost post = r.postRequest();

        CloseableHttpClient client = HttpClients.createDefault();

        HttpResponse response = client.execute(post);
        System.out.println(response);
        String respStr = EntityUtils.toString(response.getEntity());
        InputStream content = response.getEntity().getContent();
        System.out.println(respStr);
        assertEquals(200, response.getStatusLine().getStatusCode());

    }
}