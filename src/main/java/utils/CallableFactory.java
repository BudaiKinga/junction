package utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import request.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CallableFactory {
    static AtomicInteger counter = new AtomicInteger(0);

    public static List<Callable<String>> buildCallables(List<Request> requests) {
        List<Callable<String>> callables = new ArrayList<>();
        for (Request request : requests) {
            callables.add(createCallable(request));
        }
        return callables;
    }

    private static Callable<String> createCallable(Request request) {
        return () -> {
            HttpPost post = request.postRequest();

            CloseableHttpClient client = HttpClients.createDefault();

            HttpResponse response = client.execute(post);
            System.out.println("Received response for request " + request.getTimeStart() + " counter " + counter.getAndIncrement());

            return EntityUtils.toString(response.getEntity());
        };
    }
}
