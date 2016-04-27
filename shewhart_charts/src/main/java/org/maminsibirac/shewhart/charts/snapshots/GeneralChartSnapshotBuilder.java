package org.maminsibirac.shewhart.charts.snapshots;

import org.maminsibirac.shewhart.charts.Measurement;

import java.util.ArrayList;
import java.util.List;

public class GeneralChartSnapshotBuilder<TKey extends Comparable<TKey>> implements ChartSnapshotBuilder<TKey> {
    private double centralLine;
    private double upperCentralLine;
    private double lowerCentralLine;
    private List<Measurement<TKey, Double>> chartValues;

    private GeneralChartSnapshotBuilder() {}

    public static <TKey extends Comparable<TKey>> GeneralChartSnapshotBuilder<TKey> create() {
        return new GeneralChartSnapshotBuilder<>();
    }

    @Override
    public ChartSnapshot<TKey> build() {
        return GeneralChartSnapshot.create(centralLine, upperCentralLine, lowerCentralLine, chartValues);
    }

    @Override
    public ChartSnapshotBuilder<TKey> withCentralLine(double centralLine) {
        this.centralLine = centralLine;
        return this;
    }

    @Override
    public ChartSnapshotBuilder<TKey> withUpperCentralLine(double upperCentralLine) {
        this.upperCentralLine = upperCentralLine;
        return this;
    }

    @Override
    public ChartSnapshotBuilder<TKey> withLowerCentralLine(double lowerCentralLine) {
        this.lowerCentralLine = lowerCentralLine;
        return this;
    }

    @Override
    public ChartSnapshotBuilder<TKey> withChartValues(List<Measurement<TKey, Double>> chartValues) {
        this.chartValues = new ArrayList<>(chartValues);
        return this;
    }
}
