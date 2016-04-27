package org.maminsibirac.shewhart.charts.snapshots;

import org.maminsibirac.shewhart.charts.Measurement;

import java.util.List;

public interface ChartSnapshotBuilder<TKey extends Comparable<TKey>> {
    ChartSnapshotBuilder<TKey> withCentralLine(double centralLine);
    ChartSnapshotBuilder<TKey> withUpperCentralLine(double upperCentralLine);
    ChartSnapshotBuilder<TKey> withLowerCentralLine(double lowerCentralLine);
    ChartSnapshotBuilder<TKey> withChartValues(List<Measurement<TKey, Double>> chartValues);
    ChartSnapshot<TKey> build();
}
