package processor;

import request.Request;
import utils.CallableFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

public class ParallelRequestExecutor {

    private final ExecutorService pool;
    public static final String STOP = "stop";


    public ParallelRequestExecutor(int poolSize) {
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void execute(List<Request> requests, BlockingQueue<String> retrievedJsons) throws InterruptedException {
        List<Callable<Void>> callables = CallableFactory.buildCallables(requests, retrievedJsons);
        pool.invokeAll(callables);
        pool.shutdown();
        retrievedJsons.add(STOP);
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
