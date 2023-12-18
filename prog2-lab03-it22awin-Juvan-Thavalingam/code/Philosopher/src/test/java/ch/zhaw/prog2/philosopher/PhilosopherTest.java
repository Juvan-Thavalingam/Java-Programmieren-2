package ch.zhaw.prog2.philosopher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PhilosopherTest {
    private static final int PHILOSOPHER_COUNT = 5;
    private static final int BASE_TIME = 75; // milliseconds
    private  PhilosopherTable table;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testPhilosopherTable() {
        table = new PhilosopherTable(PHILOSOPHER_COUNT, BASE_TIME);
        TableStateObserver tableStateObserver = new TableStateObserver(table);
        try {
            table.start();
            int timePassed = 0;
            while (!table.isDeadlock() && timePassed < 55) {
                TimeUnit.SECONDS.sleep(5);
                timePassed += 5;
            }
        } catch (InterruptedException e) {
            fail("Table interrupted", e);
        } finally {
            table.deleteObserver(tableStateObserver);
            assertFalse(table.isDeadlock(),"Deadlock detected: " + table);
            table.stop();
        }
    }


    static class TableStateObserver implements Observer {
        final static boolean VERBOSE = false;
        final PhilosopherTable table;

        public TableStateObserver(PhilosopherTable table) {
            this.table = table;
            table.addObserver(this);
        }

        public void update(Observable o, Object arg) {
            PhilosopherTable.Philosopher philosopher = arg != null ? (PhilosopherTable.Philosopher) arg : null;
            if (VERBOSE) printState(philosopher);
            if (table.isDeadlock()) {
                fail("Deadlock detected: " + table);
            } else if (!table.isRunning()) {
                fail("Table stopped for other reason: " + table);
            }
        }

        private void printState(PhilosopherTable.Philosopher philosopher) {
            if (philosopher == null) {
                System.out.println("Application starting");
                return;
            }
            System.out.println("Philosopher " + philosopher.getId() + " " + getStateString(philosopher));
        }

        private String getStateString(PhilosopherTable.Philosopher philosopher) {
            return switch (philosopher.getState()) {
                case EATING -> "starts eating";
                case THINKING -> "starts thinking";
                case HUNGRY -> "is getting hungry";
            };
        }
    }

}
