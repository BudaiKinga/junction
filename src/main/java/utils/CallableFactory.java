package utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import request.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CallableFactory {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static List<Callable<Void>> buildCallables(List<Request> requests, BlockingQueue<String> retrievedJsons) {
        List<Callable<Void>> callables = new ArrayList<>();
        for (Request request : requests) {
            callables.add(createCallable(request, retrievedJsons));
        }
        return callables;
    }

    private static Callable<Void> createCallable(Request request, BlockingQueue<String> retrievedJsons) {
        return () -> {
            HttpPost post = request.postRequest();

            CloseableHttpClient client = HttpClients.createDefault();

            HttpResponse response = client.execute(post);
            System.out.println("Received response for request " + request.getTimeStart() + " counter " + counter.getAndIncrement());

            retrievedJsons.add(EntityUtils.toString(response.getEntity()));
            return null;
        };
    }
}
