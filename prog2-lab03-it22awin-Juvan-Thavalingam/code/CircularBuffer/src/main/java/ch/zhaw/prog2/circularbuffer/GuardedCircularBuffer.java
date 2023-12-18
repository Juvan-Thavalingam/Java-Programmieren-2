package ch.zhaw.prog2.circularbuffer;

public class GuardedCircularBuffer<T> implements Buffer<T> {
    private final CircularBuffer<T> buffer;

    public GuardedCircularBuffer(Class<T> clazz, int bufferSize) {
        buffer = new CircularBuffer<>(clazz, bufferSize);
    }

    public boolean put(T item) throws InterruptedException {
        return buffer.put(item);
    }

    public T get() throws InterruptedException {
        return buffer.get();
    }

    public void printBufferSlots() {
        buffer.printBufferSlots();
    }

    public void printBufferContent() {
        buffer.printBufferContent();
    }

    public boolean empty() {
        return false;
    }

    public boolean full() {
        return true;
    }

    public int count() {
        return 0;
    }
}
