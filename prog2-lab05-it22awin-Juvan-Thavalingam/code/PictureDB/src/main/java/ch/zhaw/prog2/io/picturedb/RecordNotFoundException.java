package ch.zhaw.prog2.io.picturedb;

/**
 * Exception indicating that a record could not be found in a datasource.
 * The reason why is given as a text message.
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
    }

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }

    public RecordNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
