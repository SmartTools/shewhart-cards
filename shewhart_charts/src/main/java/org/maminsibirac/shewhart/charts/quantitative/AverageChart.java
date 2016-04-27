package org.maminsibirac.shewhart.charts.quantitative;

import org.maminsibirac.shewhart.charts.Coefficients;
import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.SmartControlChart;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.groups.quantitative.ARChartsControlGroup;
import org.maminsibirac.shewhart.modules.notification.NotificationChartModule;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

public class AverageChart<TData extends Number, TKey extends Comparable<TKey>>
        extends SmartControlChart<TKey, ARChartsControlGroup<TData, TKey>> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private AverageChart(
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
    AverageChart<TData, TKey> create(
            Coefficients coefficients,
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        checkOnNull(coefficients, "");
        checkOnNull(snapshotFactory, "");

        return new AverageChart<>(coefficients, snapshotFactory, storageChartModule,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(List<ARChartsControlGroup<TData, TKey>> controlGroups) {
        checkOnNull(controlGroups, "");

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        Double centralLine = calculateCentralLine(values);
        double coefficient = calculateCoefficient(controlGroups, coefficients.get(controlGroups.get(0).size(), "A2"));
        Double upperCentralLine = calculateUpperCentralLine(centralLine, coefficient);
        Double lowerCentralLine = calculateLowerCentralLine(centralLine, coefficient);

        return snapshotBuilder
                .withCentralLine(centralLine)
                .withUpperCentralLine(upperCentralLine)
                .withLowerCentralLine(lowerCentralLine)
                .withChartValues(values)
                .build();
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ARChartsControlGroup<TData, TKey>> groups) {
        List<Measurement<TKey, Double>> resultValues = new LinkedList<>();
        for (ARChartsControlGroup<TData, TKey> group : groups) {
            resultValues.add(Measurement.create(
                    group.getKey(),
                    calculateAverage(group
                            .values()
                            .stream()
                            .map(TData::doubleValue)
                            .collect(Collectors.toList())))
            );
        }

        return resultValues;
    }

    @Override
    protected double calculateCentralLine(List<Measurement<TKey, Double>> values) {
        return calculateAverage(values
                .stream()
                .map(Measurement<TKey, Double>::getValue)
                .collect(Collectors.toList())
        );
    }

    @Override
    protected double calculateUpperCentralLine(double centralLine, double coefficient) {
        return centralLine + coefficient;
    }

    @Override
    protected double calculateLowerCentralLine(double centralLine, double coefficient) {
        return centralLine - coefficient;
    }

    private double calculateCoefficient(
            List<ARChartsControlGroup<TData, TKey>> controlGroups,
            double tabularCoefficient
    ) {
        double averageRange = calculateAverage(controlGroups
                .stream()
                .map(group -> calculateRange(group
                        .values()
                        .stream()
                        .map(TData::doubleValue)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList())
        );

        return averageRange * tabularCoefficient;
    }

    private Double calculateRange(List<Double> values) {
        Double max = Collections.max(values);
        Double min = Collections.min(values);

        return max - min;
    }

    private double calculateAverage(List<Double> values) {
        Double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }

        return sum / values.size();
    }

}
