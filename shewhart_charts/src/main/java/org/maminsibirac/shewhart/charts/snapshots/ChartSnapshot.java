package org.maminsibirac.shewhart.charts.snapshots;

import org.maminsibirac.shewhart.charts.Measurement;

import java.util.List;
import java.util.Map;

/**
 * A snapshots chart by a some moment.
 * @author Nikita Korytov.
 */
public interface ChartSnapshot<TKey extends Comparable<TKey>> {
    /**
     * @return the {@link Map} object with chart's values.
     */
    List<Measurement<TKey, Double>> getChartValues();

    /**
     * @return the central line of chart.
     */
    Double getCentralLine();

    /**
     * @return the upper central line of the chart.
     */
    Double getUpperCentralLine();

    /**
     * @return the lower central line of the chart.
     */
    Double getLowerCentralLine();
}
