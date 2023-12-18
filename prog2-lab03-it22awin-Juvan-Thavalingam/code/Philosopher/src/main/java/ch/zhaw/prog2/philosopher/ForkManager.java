package ch.zhaw.prog2.philosopher;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ch.zhaw.prog2.philosopher.ForkManager.ForkState.*;

/**
 * ForkManager manages the resources (=forks), used by the philosophers
 */
class ForkManager {
    // count maximum concurrent threads acquiring forks - used for internal statistics
    private static final LockFreeMax concurrentAcquiresCounter = new LockFreeMax();

    private final Lock managerLock; // shared lock for all forks
    private final int delayTime;    // delay in milliseconds between acquiring / releasing the forks
    private final int numForks;     // amount of forks to be managed
    private final Condition condition; // TODO
    private final Fork[] forks;     // array of managed forks

    /**
     * Constructor to initialize the fork manager.
     * @param numForks     amount of forks to manage
     * @param delayTime    delay in milliseconds between acquiring / releasing the forks
     */
    public ForkManager(int numForks, int delayTime) {
        this.managerLock = new ReentrantLock();
        this.delayTime = delayTime;
        this.numForks = numForks;
        this.condition = managerLock.newCondition();
        this.forks = new Fork[numForks];
        for (int forkId = 0; forkId < numForks; forkId++)
            forks[forkId] = new Fork(forkId, managerLock);
    }

    /**
     * Acquire both forks of a specific philosopher.
     * Here you implement your synchronization strategy.
     * There should be always a delay between taking the two forks (see {@link #forkDelay()})
     * @param philosopherId id of the philosopher
     */
    public void acquireForks(int philosopherId) throws InterruptedException {
        // acquire forks sequentially
        managerLock.lock();
        try {
            boolean forkwait = false;

            while (!forkwait){
                if(leftFork(philosopherId).state == FREE && rightFork(philosopherId).state == FREE){
                    leftFork(philosopherId).acquire(philosopherId);
                    forkDelay();
                    rightFork(philosopherId).acquire(philosopherId);
                    forkwait = true;
                    condition.signalAll();
                } else {
                    condition.await();
                }
            }
        } finally {
            managerLock.unlock();
        }
    }

    /**
     * Release both forks for a specific philosopher.
     * There should be always a delay between releasing the two forks.
     *
     * @param philosopherId id of the philosopher
     */
    public void releaseForks(int philosopherId) throws InterruptedException {
        // order of releasing does not matter
        leftFork(philosopherId).release(philosopherId);
        forkDelay();
        rightFork(philosopherId).release(philosopherId);
    }

    /**
     * Returns the right fork of the given philosopher
     * @param philosopherId id of philosopher to get the right fork
     * @return right fork of the specified philosopher
     */
    private Fork rightFork(int philosopherId) {
        return forks[(numForks + philosopherId - 1) % numForks];
    }

    /**
     * Returns the left fork of the given philosopher
     * @param philosopherId id of philosopher to get the left fork
     * @return left fork of the specified philosopher
     */
    private Fork leftFork(int philosopherId) {
        return forks[philosopherId];
    }

    /**
     * Waits {@link #delayTime} milliseconds between taking the forks.
     * @throws InterruptedException if the thread is interrupted during wait
     */
     void forkDelay() throws InterruptedException {
        try {
            Thread.sleep(this.delayTime);
        } catch (InterruptedException e) {
            throw new InterruptedException("Interrupted fork delay - " + e.getMessage());
        }
    }

    /**
     * Get the maximum number of threads concurrently acquired forks - used to measure parallelism.
     * @return maximum number of concurrent threads acquiring forks
     */
    public int getConcurrentAcquires() {
        return concurrentAcquiresCounter.maxValue.intValue();
    }

    /**
     * Test if all forks are in a specific state. Used to detect deadlocks.
     * @param state State of fork to test for
     * @return true if all forks are in the given state, false otherwise
     */
    public boolean areAllForksInState(ForkState state) {
    	return Arrays.stream(forks).allMatch(fork -> fork.state == state);
    }

    @Override
    public String toString() {
        return "forks = " + Arrays.toString(forks);
    }

    /**
     * Possible states of a fork.
     * The WAITING state is used as a temporary state to mark an OCCUPIED fork,
     * if another philosopher is waiting for it (see {@link Fork#acquire(int)}.
     */
    enum ForkState {
        FREE, WAITING, OCCUPIED
    }

    /**
     * Class holding the state of a single fork.
     * It also provides a condition (waiting room) to allow philosophers to wait for this fork.
     */
    private static class Fork {
        private final int id;             // unique id of fork
        private final Lock managerLock;   // shared lock of the fork-manager
        private final Condition forkFree; // specific wait condition for this fork
        private ForkState state = FREE;   // current state of the fork
        private int ownerId;              // temporary owning philosopher of the fork

        /**
         * Constructor for a fork.
         * @param id    unique id of the fork
         * @param managerLock the ForkManager's shared lock to use for wait conditions
         */
        public Fork(int id, Lock managerLock) {
            this.id = id;
            this.ownerId = -1;
            this.managerLock = managerLock;
            this.forkFree = managerLock.newCondition();
        }

        /**
         * Acquire the fork for a specific philosopher (applicantId).
         * The applicantId is used to remember the current "owner" of a fork.
         * @param applicantId id of the philosopher to acquire the fork
         * @throws InterruptedException if the thread is interrupted during waiting for the fork
         */
        public void acquire(int applicantId) throws InterruptedException {
            try {
                managerLock.lock();
                while (state != FREE) {
                    state = WAITING;
                    forkFree.await();
                }
                concurrentAcquiresCounter.increment();
                TimeUnit.MILLISECONDS.sleep(10);
                state = OCCUPIED;
                ownerId = applicantId;
            } catch (InterruptedException e) {
                throw new InterruptedException("Interrupted acquire fork " + id + " - " + e.getMessage());
            } finally {
                concurrentAcquiresCounter.decrement();
                managerLock.unlock();
            }
        }

        /**
         * Release the fork for a specific philosopher (applicantId).
         * The applicantId is used to verify that a fork is released by the "owner".
         * @param applicantId id of the philosopher releasing the fork
         * @throws InterruptedException if the thread is interrupted during releasing the fork
         */
        public void release(int applicantId) throws InterruptedException {
            try {
                managerLock.lock();
                if (ownerId != applicantId) throw new IllegalStateException("Release fork " + id + " not owned by " + applicantId);
                TimeUnit.MILLISECONDS.sleep(10);
                state = FREE;
                ownerId = -1;
                forkFree.signal();
            } catch (InterruptedException e) {
                throw new InterruptedException("Interrupted release fork " + id + " - " + e.getMessage());
            } finally {
                managerLock.unlock();
            }
        }

        @Override
        public String toString() {
            return "Fork {" + "id=" + id + ", state=" + state + ", owner=" + ownerId + '}';
        }
    }

    /*
     * Determine maximum value without using locks
     *
     * This class is used here to detect the maximum numbers of threads that have been active in the same codeblock.
     * To get a good measurement:
     *  - Use a sleep() between increment() and decrement()
     *  - Use increment() after acquiring the lock and decrement() before releasing the lock
     *  - Do not call any method that does release the lock: Do not use await() between increment() and decrement()
     */
    private static class LockFreeMax {
        private final AtomicInteger value = new AtomicInteger(0);
        private final AtomicInteger maxValue = new AtomicInteger(0);

        public void increment() {
            maxValue.accumulateAndGet(value.incrementAndGet(), Math::max);
        }

        public void decrement() {
            value.decrementAndGet();
        }
    }
}
