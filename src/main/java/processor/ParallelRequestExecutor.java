package processor;

import request.Request;
import utils.CallableFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParallelRequestExecutor {

    private final ExecutorService pool;

    public ParallelRequestExecutor(int poolSize) {
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public List<String> execute(List<Request> requests) throws InterruptedException {
        List<Callable<String>> callables = CallableFactory.buildCallables(requests);
        List<Future<String>> results = pool.invokeAll(callables);
        pool.shutdown();

        return results.stream()
                .map(getResponseFromFuture())
                .collect(Collectors.toList());
    }

    private Function<Future<String>, String> getResponseFromFuture() {
        return res -> {
            try {
                return res.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Out of time...");
            return null;
        };
    }
}
