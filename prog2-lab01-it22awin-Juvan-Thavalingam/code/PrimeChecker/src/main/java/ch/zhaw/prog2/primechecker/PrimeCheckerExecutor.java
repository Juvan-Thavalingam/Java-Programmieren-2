package ch.zhaw.prog2.primechecker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PrimeCheckerExecutor {

    private static final long LOWER_LIMIT = 10000L;
    private static final long UPPER_LIMIT = 1000000000L;
    private static final int NUM_PRIME = 500;

    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        long duration;
        try {
            checkPrimes(NUM_PRIME);
        } catch (InterruptedException e) {
            System.out.println("Interrupted - " + e.getMessage());
        } finally {
            duration = System.currentTimeMillis() - starttime;
        }
        System.out.println("Finished in " + duration + " ms");
    }

    private static void checkPrimes(int numPrimes) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        // TODO: create ExecutorService - What ThreadPool-Type/-Size fits best?

        for (int i = 0; i < numPrimes; i++) {
            // TODO: execute the runnable using the executor service
            executorService.execute(new PrimeTask(nextRandom()));
        }
        // stop ExecutorService
        executorService.shutdown();
        // wait for termination with timeout of 1 minute
        if(!executorService.awaitTermination(1, TimeUnit.MINUTES)){
            executorService.shutdownNow();
            System.out.println("Terminated");
        }
    }

    private static long nextRandom() {
        return LOWER_LIMIT + (long)(Math.random() * (UPPER_LIMIT - LOWER_LIMIT));
    }
}
