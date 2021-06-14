package pers.zhc.web.secure;

/**
 * @author bczhc
 */
public class InvalidDataException extends Exception {
    public InvalidDataException() {
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(Throwable cause) {
        super(cause);
    }
}
