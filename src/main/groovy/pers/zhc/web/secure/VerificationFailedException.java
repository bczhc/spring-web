package pers.zhc.web.secure;

/**
 * @author bczhc
 */
public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException() {
    }

    public VerificationFailedException(String message) {
        super(message);
    }

    public VerificationFailedException(Throwable cause) {
        super(cause);
    }
}
