package info.smart_tools.shewhart_charts.modules;

public class CorruptedModuleException extends RuntimeException {
    public CorruptedModuleException(String message) {
        super(message);
    }

    public CorruptedModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
