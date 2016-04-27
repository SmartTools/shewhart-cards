package org.maminsibirac.shewhart.charts.quantitative;

import org.maminsibirac.shewhart.charts.SmartControlChart;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.groups.quantitative.ISRChartsControlGroup;
import org.maminsibirac.shewhart.modules.notification.NotificationChartModule;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.charts.Coefficients;
import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.utils.ValidationUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IndividualChart<TData extends Number, TKey extends Comparable<TKey>>
        extends SmartControlChart<TKey, ISRChartsControlGroup<TData, TKey>> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private IndividualChart(
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
    IndividualChart<TData, TKey> create(
            Coefficients coefficients,
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(coefficients, "");
        ValidationUtils.checkOnNull(snapshotFactory, "");

        return new IndividualChart<>(coefficients, snapshotFactory, storageChartModule,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(List<ISRChartsControlGroup<TData, TKey>> controlGroups) {
        ValidationUtils.checkOnNull(controlGroups, "");

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        Double centralLine = calculateCentralLine(values);
        double coefficient = calculateCoefficient(controlGroups, coefficients.get(2, "E2"));
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
    protected List<Measurement<TKey, Double>> calculateValues(List<ISRChartsControlGroup<TData, TKey>> controlGroups) {
        return controlGroups
                .stream()
                .map(group -> Measurement.create(group.getKey(), group.get().doubleValue()))
                .collect(Collectors.toList());
    }

    @Override
    protected double calculateCentralLine(List<Measurement<TKey, Double>> values) {
        return calculateAverage(values
                .stream()
                .map(Measurement::getValue)
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
            List<ISRChartsControlGroup<TData, TKey>> controlGroups,
            double tabularCoefficient
    ) {
        double averageRange = calculateAverage(
                calculateRanges(controlGroups
                        .stream()
                        .map(value -> value.get().doubleValue())
                        .collect(Collectors.toList())
                )
        );

        return averageRange * tabularCoefficient;
    }

    private List<Double> calculateRanges(List<Double> values) {
        List<Double> ranges = new LinkedList<>();
        int valuesSize = values.size();
        for (int i = 1; i < valuesSize; ++i) {
            ranges.add(Math.abs(values.get(i) - values.get(i - 1)));
        }

        return ranges;
    }

    private double calculateAverage(List<Double> values) {
        Double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }

        return sum / values.size();
    }

}
