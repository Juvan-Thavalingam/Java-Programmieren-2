package ch.zhaw.prog2.heartbeat.parts;

import ch.zhaw.prog2.heartbeat.Half.Side;

public abstract class Valve {

    private boolean open;
    private Side side;

    public Valve(Side side) {
        this.side = side;
    }

    /**
     * Opens the valve
     *
     * @throws IllegalValveStateException when the valve is already open
     */
    public void open() throws IllegalValveStateException {
        if (open) {
            throw new IllegalValveStateException(this.getClass().getSimpleName() + " " + side + " valve is already open.");
        }
        System.out.println(this.getClass().getSimpleName() + " " + side + " is opening.");
        open = true;
    }

    /**
     * Closes the valve
     *
     * @throws IllegalValveStateException when valve is already closed
     */
    public void close() throws IllegalValveStateException {
        if (!open) {
            throw new IllegalValveStateException(this.getClass().getSimpleName() + " " + side + " valve is already closed.");
        }
        System.out.println(this.getClass().getSimpleName() + " " + side + " is closing.");
        open = false;
    }

    public boolean isOpen() {
        return open;
    }

    protected void setOpen(Boolean open) {
        this.open = open;
    }

    public static class IllegalValveStateException extends Exception {

        public IllegalValveStateException() {
        }

        public IllegalValveStateException(String message) {
            super(message);
        }

        public IllegalValveStateException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
