package info.smart_tools.shewhart_charts;

import info.smart_tools.shewhart_charts.sensor.SensorException;

public class ControlChartException extends SensorException {
    public ControlChartException(String message) {
        super(message);
    }

    public ControlChartException(String message, Throwable cause) {
        super(message, cause);
    }
}
