package request;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.*;

public class RawRequestTest {
    @Test
    public void postRequest() throws Exception {
        RawRequest r = new RawRequest();
        r.setCommand("list");
        r.setTimeStart(new Date(Timestamp.valueOf("2019-08-01 00:00:01").getTime()));
        r.setTimeStop(new Date(Timestamp.valueOf("2019-08-01 00:00:02").getTime()));

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