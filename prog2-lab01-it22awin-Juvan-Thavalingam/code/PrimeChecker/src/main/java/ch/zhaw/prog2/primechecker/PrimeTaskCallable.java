package ch.zhaw.prog2.primechecker;

import java.util.concurrent.Callable;

public class PrimeTaskCallable implements Callable<PrimeTaskCallable.Result> {

    private final long primeCandidate;

    public PrimeTaskCallable(long primeCandidate) {
        this.primeCandidate = primeCandidate;
    }


    public Result call() {
        return new Result(primeCandidate, findSmallestFactor(primeCandidate));
    }

    /**
     * Brute force check if submitted candidate is a prime number.
     *
     * @param primeCandidate Number to check if it is a
     * @return 0 if prime number, smallest factor otherwise
     */
    private long findSmallestFactor(long primeCandidate) {
        if (primeCandidate>3) {
            for(long factor = 2; factor <= primeCandidate/2; ++factor) {
                if ( primeCandidate / factor * factor == primeCandidate)  {
                    return factor; // found a factor -> is no prime
                }
            }
        }
        return 0; // is prime number
    }

    /**
     * Small static helper class serving as a container to return the result.
     * No accessor methods. Use direct access to fields to read values.
     * (Starting from Java14, we could use Java Records for this)
     */
    public static class Result {
        public final long candidate;
        public final long factor;
        public final boolean isPrime;

        public Result(long candidate, long factor) {
            this.candidate = candidate;
            this.factor = factor;
            this.isPrime = factor == 0;
        }
    }
}
