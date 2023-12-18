package ch.zhaw.prog2.circularbuffer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CircBufferSimulation {
    public static void main(String[] args) {
        final int bufferCapacity = 15;  // max. number of items the buffer can store
        final int producerCount  = 1;   // Number of producer threads
        final int consumerCount  = 1;   // Number of consumer threads
        final int maxProduceTime = 500; // max. production time for one item
        final int maxConsumeTime = 500; // max. consumption time for one item

        try {
            Buffer<String> buffer = new CircularBuffer<>(String.class, bufferCapacity);

            // start consumers
            Consumer[] consumers = new Consumer[consumerCount];
            for (int i = 0; i < consumerCount; i++) {
                consumers[i] = new Consumer("Consumer_" + i, buffer, maxConsumeTime);
                consumers[i].start();
            }
            // start producers
            Producer[] producers = new Producer[producerCount];
            for (int i = 0; i < producerCount; i++) {
                producers[i] = new Producer("Producer_" + i, buffer, maxProduceTime);
                producers[i].start();
            }

            // print live buffer status
            ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
            // print occupied slots of buffer every second
            scheduled.scheduleAtFixedRate(buffer::printBufferSlots, 1, 1, TimeUnit.SECONDS);
            // print content of buffer every 5 seconds
            scheduled.scheduleAtFixedRate(buffer::printBufferContent, 5,5, TimeUnit.SECONDS);


            System.out.println("Press Enter to terminate");
            System.in.read();

            System.out.println("Shutting down ...");
            // shutdown producers
            for (Producer producer : producers) {
                producer.terminate();
            }
            // shutdown consumers
            for (Consumer consumer : consumers) {
                consumer.terminate();
            }
            // shutdown statistics
            scheduled.shutdown();
            System.out.println("Simulation ended.");

        } catch (Exception logOrIgnore) {
            System.out.println(logOrIgnore.getMessage());
        }
    }

    private static class Producer extends Thread {
        private volatile boolean running = true;
        private final Buffer<String> buffer;
        private final int maxProduceTime;

        public Producer(String name, Buffer<String> buffer, int maxProduceTime) {
            super(name);
            this.buffer = buffer;
            this.maxProduceTime = maxProduceTime;
        }

        public void terminate() {
            running = false;
        }

        @Override
        public void run() {
            running = true;
            int number = 1;
            try {
                while (running) {
                    buffer.put("#" + number++);
                    sleep((int) (100 + Math.random() * maxProduceTime));
                }
            } catch (InterruptedException ex) {
                System.err.println("Interrupted in " + getName() + ": " + ex.getMessage());
            }
        }
    }

    private static class Consumer extends Thread {
        private volatile boolean running = true;
        private final Buffer<String> buffer;
        private final int maxConsumeTime;

        public Consumer(String name, Buffer<String> buffer, int maxConsumeTime) {
            super(name);
            this.buffer = buffer;
            this.maxConsumeTime = maxConsumeTime;
        }

        public void terminate() {
            running = false;
        }

        @Override
        public void run() {
            running = true;
            try {
                while (running) {
                    buffer.get();
                    Thread.sleep(100 + (int) (Math.random() * maxConsumeTime));
                }
            } catch (InterruptedException ex) {
                System.err.println("Interrupted in " + getName() + ": " + ex.getMessage());
            }
        }
    }

}
