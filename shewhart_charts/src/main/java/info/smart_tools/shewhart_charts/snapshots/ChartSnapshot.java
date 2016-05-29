package info.smart_tools.shewhart_charts.snapshots;

import info.smart_tools.shewhart_charts.utils.Measurement;

import java.util.List;
import java.util.Map;

public interface ChartSnapshot<TKey extends Comparable<TKey>> {
    List<Measurement<TKey, Double>> getChartValues();
    Double getCentralLine();
    Double getUpperCentralLine();
    Double getLowerCentralLine();
}
