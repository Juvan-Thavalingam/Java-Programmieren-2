package ch.zhaw.prog2.primechecker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PrimeCheckerFuture {

    private static final long LOWER_LIMIT = 10000L;
    private static final long UPPER_LIMIT = 1000000000L;
    private static final int NUM_PRIME = 500;

    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        long duration;
        try {
            checkPrimes(NUM_PRIME);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Interrupted - " + e.getMessage());
        } finally {
            duration = System.currentTimeMillis() - starttime;
        }
        System.out.println("Finished in " + duration + " ms");
    }

    private static void checkPrimes(int numPrimes) throws InterruptedException, ExecutionException {
        List<Future<PrimeTaskCallable.Result>> futureList = new ArrayList<>();

        // TODO: create ExecutorService
        ExecutorService executorService = Executors.newCachedThreadPool();

        // TODO: submit tasks to ExecutorService and collect the returned Futures in a List
        for (int i = 0; i < numPrimes; i++) {
            futureList.add(executorService.submit(new PrimeTaskCallable(nextRandom())));
        }
        // TODO: Loop through List, wait for completion and print results
        for (Future<PrimeTaskCallable.Result> future : futureList){
            try{
                PrimeTaskCallable.Result result = future.get();
                System.out.println("Number: " + result.candidate + "  -> " +
                    (result.isPrime ? "PRIME" : "Factor: " + result.factor));
            } catch (InterruptedException e){
                throw new InterruptedException(e.getMessage());
            }
        }

        // TODO: stop ExecutorService
        executorService.shutdown();
        // TODO: await termination with timeout 1 minute
        while(!executorService.awaitTermination(1,TimeUnit.MINUTES)){
            System.out.println("Failed");
            executorService.shutdown();
        }
    }

    private static long nextRandom() {
        return LOWER_LIMIT + (long)(Math.random() * (UPPER_LIMIT - LOWER_LIMIT));
    }
}
