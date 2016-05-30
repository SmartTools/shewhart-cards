package info.smart_tools.shewhart_charts.modules.notification;

public class NotificationException extends Exception {
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationException(String message) {
        super(message);
    }
}
