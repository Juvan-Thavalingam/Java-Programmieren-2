package ch.zhaw.prog2.primechecker;

public class PrimeChecker extends Thread{

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
        for (int i = 0; i < numPrimes; i++) {
            Runnable runnable = new PrimeTask(nextRandom()); // runs sequential in current thread
            new Thread(runnable).start();
        }
    }

    private static long nextRandom() {
        return LOWER_LIMIT + (long)(Math.random() * (UPPER_LIMIT - LOWER_LIMIT));
    }
}
