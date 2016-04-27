package org.maminsibirac.shewhart.charts.quantitative;

import org.maminsibirac.shewhart.charts.Coefficients;
import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.SmartControlChart;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.groups.quantitative.ISRChartsControlGroup;
import org.maminsibirac.shewhart.modules.notification.NotificationChartModule;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

public class SlideRangeChart <TData extends Number, TKey extends Comparable<TKey>>
        extends SmartControlChart<TKey, ISRChartsControlGroup<TData, TKey>> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private SlideRangeChart(
            Coefficients coefficients,
            ChartSnapshotBuilder<TKey> snapshotBuilder,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        super(storageChartModule, verificationChartModule, notificationChartModule);
        this.coefficients = coefficients;
        this.snapshotBuilder = snapshotBuilder;
    }

    public static <TData extends Number, TKey extends Comparable<TKey>>
    SlideRangeChart<TData, TKey> create(
            Coefficients coefficients,
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        checkOnNull(coefficients, "");
        checkOnNull(snapshotFactory, "");

        return new SlideRangeChart<>(coefficients, snapshotFactory, storageChartModule,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(List<ISRChartsControlGroup<TData, TKey>> controlGroups) {
        checkOnNull(controlGroups, "");

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        Double centralLine = calculateCentralLine(values);
        Double upperCentralLine = calculateUpperCentralLine(centralLine,
                coefficients.get(2, "D4"));
        Double lowerCentralLine = calculateLowerCentralLine(centralLine,
                coefficients.get(2, "D3"));

        return snapshotBuilder
                .withCentralLine(centralLine)
                .withUpperCentralLine(upperCentralLine)
                .withLowerCentralLine(lowerCentralLine)
                .withChartValues(values)
                .build();
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ISRChartsControlGroup<TData, TKey>> controlGroups) {
        return calculateRanges(controlGroups
                .stream()
                .map(group -> Measurement.create(group.getKey(), group.get().doubleValue()))
                .collect(Collectors.toList())
        );

    }

    @Override
    protected double calculateCentralLine(List<Measurement<TKey, Double>> values) {
        return calculateAverage(values);
    }

    @Override
    protected double calculateUpperCentralLine(double centralLine, double coefficient) {
        return centralLine * coefficient;
    }

    @Override
    protected double calculateLowerCentralLine(double centralLine, double coefficient) {
        return centralLine * coefficient;
    }

    private List<Measurement<TKey, Double>> calculateRanges(List<Measurement<TKey, Double>> values) {
        List<Measurement<TKey, Double>> ranges = new LinkedList<>();
        int valuesSize = values.size();
        for (int i = 1; i < valuesSize; ++i) {
            ranges.add(Measurement.create(
                    values.get(i).getKey(),
                    values.get(i).getValue() - values.get(i - 1).getValue())
            );
        }

        return ranges;
    }

    private double calculateAverage(List<Measurement<TKey, Double>> values) {
        Double sum = 0.0;
        for (Measurement<TKey, Double> value : values) {
            sum += value.getValue();
        }

        return sum / values.size();
    }

}
