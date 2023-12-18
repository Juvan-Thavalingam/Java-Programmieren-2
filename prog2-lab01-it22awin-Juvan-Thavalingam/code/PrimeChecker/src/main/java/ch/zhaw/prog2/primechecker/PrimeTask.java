package ch.zhaw.prog2.primechecker;

public class PrimeTask implements Runnable {

    private final long primeCandidate;

    public PrimeTask(long primeCandidate) {
        this.primeCandidate = primeCandidate;
    }

    public void run() {
        long smallestFactor = findSmallestFactor(primeCandidate);
        System.out.println("Number: " + this.primeCandidate + "  -> " +
            ( smallestFactor == 0 ? "PRIME" : "Factor: "  + smallestFactor ));
    }
    /**
     * Brute force check if submitted candidate is a prime number.
     *
     * @param primeCandidate Number to check if it is a
     * @return 0 if prime number, smallest factor otherwise
     */
    private long findSmallestFactor(long primeCandidate) {
        if (primeCandidate>3) {
            for(long factor = 2; factor <=primeCandidate/2; ++factor) {
                if ( primeCandidate / factor * factor == primeCandidate)  {
                    return factor; // found a factor -> is no prime
                }
            }
        }
        return 0; // is prime number
    }
}
