package info.smart_tools.shewhart_charts.modules.verification;

public class VerificationException extends Exception {
    public VerificationException(String message) {
        super(message);
    }

    public VerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
