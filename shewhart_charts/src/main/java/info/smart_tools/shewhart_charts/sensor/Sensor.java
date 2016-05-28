package info.smart_tools.shewhart_charts.sensor;

public interface Sensor<Group> {
    void update(Group data) throws SensorException;
    void report() throws SensorException;
}
