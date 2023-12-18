package ch.zhaw.prog2.circularbuffer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GuardedCircularBufferTest {
    private static final int BUFFER_SIZE = 5;
    private static final String DEFAULT_ITEM = "Item";
    private GuardedCircularBuffer<String> buffer;

    @BeforeEach
    void setUp() {
        buffer = new GuardedCircularBuffer<>(String.class, BUFFER_SIZE);
    }

    @AfterEach
    void tearDown() {
        buffer = null;
    }

    @Test
    void testEmpty() {
        assertTrue(buffer.empty(), "Must return true if buffer is empty");
        assertDoesNotThrow(() -> { buffer.put("Some content"); }, "Must not throw an exception");
        assertFalse(buffer.empty(), "Must return false if buffer is not empty");
    }

    @Test
    void testFull() {
        assertFalse(buffer.full(), "Must return false if buffer is empty");
        for(int num=0; num < BUFFER_SIZE; num++) {
            String item = DEFAULT_ITEM + " " + num;
            assertDoesNotThrow(() -> { buffer.put(item); }, "Must not throw an exception");
        }
        assertTrue(buffer.full(), "Must return true if buffer is full");
        assertDoesNotThrow(() -> { buffer.get(); }, "Must not throw an exception");
        assertFalse(buffer.full(), "Must return false if buffer is not full");
    }

    @Test
    void testCount() {
        assertEquals(0, buffer.count(), "Initial should be 0");
        for(int num=1; num <= BUFFER_SIZE; num++) {
            String item = DEFAULT_ITEM + " " + num;
            assertDoesNotThrow(() -> { buffer.put(item); }, "Must not throw an exception");
            assertEquals(num, buffer.count());
        }
    }


    @Test
    void testSinglePutGet() {
        assertTrue(buffer.empty(), "Make sure buffer is empty");
        assertDoesNotThrow(() -> { buffer.put(DEFAULT_ITEM); }, "Must not throw an exception");
        assertEquals(1, buffer.count());
        AtomicReference<String> returnItem = new AtomicReference<>();
        assertDoesNotThrow(() -> returnItem.set(buffer.get()), "Must not throw an exception");
        assertEquals(DEFAULT_ITEM, returnItem.get());
    }

    @Test
    void testMultiplePutGet() {
        assertTrue(buffer.empty(), "Make sure buffer is empty");
        // write items
        for (int num = 0; num < BUFFER_SIZE; num++) {
            String item = DEFAULT_ITEM + " " + num;
            assertDoesNotThrow(() -> {
                buffer.put(item);
            }, "Must not throw an exception");
        }
        // read items in same order
        for (int num = 0; num < BUFFER_SIZE; num++) {
            AtomicReference<String> returnItem = new AtomicReference<>();
            assertDoesNotThrow(() -> returnItem.set(buffer.get()), "Must not throw an exception");
            assertEquals(DEFAULT_ITEM + " " + num, returnItem.get());
        }
    }



    @Test
    void testBlockProduce() {
        assertTrue(buffer.empty(), "Make sure buffer is empty");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(new Producer<>(buffer, BUFFER_SIZE + 1, DEFAULT_ITEM));
            TimeUnit.MILLISECONDS.sleep(100);
            assertTrue(buffer.full(), "Buffer should be full");
            executorService.shutdown();
            assertFalse(executorService.awaitTermination(3, TimeUnit.SECONDS), "Executor should be blocking");

        } catch (InterruptedException e) {
            fail("Interrupted executor", e);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void testBlockConsume() {
        assertTrue(buffer.empty(), "Make sure buffer is empty");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(new Consumer<>(buffer, 2));
            TimeUnit.MILLISECONDS.sleep(100);
            assertTrue(buffer.empty(), "Buffer should be empty");
            executorService.shutdown();
            assertFalse(executorService.awaitTermination(3, TimeUnit.SECONDS), "Executor should be blocking");
        } catch (InterruptedException e) {
            fail("Interrupted executor", e);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void testProduceThenConsume()  {
        assertTrue(buffer.empty(), "Make sure buffer is empty");
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Producer<>(buffer, BUFFER_SIZE, DEFAULT_ITEM));
            TimeUnit.MILLISECONDS.sleep(100);
            assertEquals(BUFFER_SIZE, buffer.count(), "Buffer must contain " + BUFFER_SIZE + " items");
            Consumer<String> consumer = new Consumer<>(buffer, BUFFER_SIZE);
            executorService.execute(consumer);
            TimeUnit.MILLISECONDS.sleep(100);
            assertEquals(0, buffer.count(), "Buffer must contain 0 items");
            Object[] expected = Stream.generate(() -> DEFAULT_ITEM).limit(BUFFER_SIZE).toArray();
            assertArrayEquals(expected, consumer.getItems().toArray());
            executorService.shutdown();
            assertTrue(executorService.awaitTermination(3, TimeUnit.SECONDS), "Timeout shutting down Executor");
        } catch (InterruptedException e) {
            fail("Interrupted executor", e);
        }

    }

    @Test
    void testProduceAndConsume()  {
        assertTrue(buffer.empty(), "Make sure buffer is empty");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.execute(new Producer<>(buffer, 10 * BUFFER_SIZE, "Item"));
            TimeUnit.SECONDS.sleep(1);
            Consumer<String> consumer = new Consumer<>(buffer, 10*BUFFER_SIZE);
            executorService.execute(consumer);
            TimeUnit.SECONDS.sleep(1);
            List<String> receivedItems = consumer.getItems();
            assertEquals(10*BUFFER_SIZE, receivedItems.size());
            Object[] expected = Stream.generate(() -> DEFAULT_ITEM).limit(10*BUFFER_SIZE).toArray();
            assertArrayEquals(expected, consumer.getItems().toArray());
            executorService.shutdown();
            assertTrue(executorService.awaitTermination(3, TimeUnit.SECONDS), "Timeout shutting down Executor");
        } catch (InterruptedException e) {
            fail("Interrupted executor", e);
        } finally {
            executorService.shutdownNow();
        }

    }


    private class Producer<T> implements Runnable {
        private final GuardedCircularBuffer<T> buffer;
        private final int numItems;
        private final T item;

        public Producer(GuardedCircularBuffer<T> buffer, int numItems, T item) {
            this.buffer = buffer;
            this.numItems = numItems;
            this.item = item;
        }

        @Override
        public void run() {
            for (int num = 0; num < numItems; num++) {
                try {
                    buffer.put(item);
                } catch (InterruptedException e) {
                    System.err.println("Interrupted Producer at " + num + ": " + e.getMessage());
                }
            }
        }
    }

    private class Consumer<T> implements Runnable {
        private final GuardedCircularBuffer<T> buffer;
        private final int numItems;
        private final List<T> items;

        public Consumer(GuardedCircularBuffer<T> buffer, int numItems) {
            this.buffer = buffer;
            this.numItems = numItems;
            this.items = new ArrayList<>();
        }

        public List<T> getItems() {
            return items;
        }

        @Override
        public void run() {
            for (int num = 0; num < numItems; num++) {
                try {
                    this.items.add(buffer.get());
                } catch (InterruptedException e) {
                    System.err.println("Interrupted Consumer at " + num + ": " + e.getMessage());
                }
            }
        }
    }
}
