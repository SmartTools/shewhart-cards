package info.smart_tools.shewhart_charts.quantitative;

import info.smart_tools.shewhart_charts.ControlChartException;
import info.smart_tools.shewhart_charts.SmartControlChart;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationChartModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageChartGroup;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.utils.Coefficients;
import info.smart_tools.shewhart_charts.utils.GroupFieldHelper;
import info.smart_tools.shewhart_charts.utils.Measurement;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

public class AverageChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<Double, TKey> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private AverageChart(
            Coefficients coefficients,
            ChartSnapshotBuilder<TKey> snapshotBuilder,
            StorageChartGroup<TKey, Double> storageChartGroup,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        super(storageChartGroup, verificationChartModule, notificationChartModule);
        this.coefficients = coefficients;
        this.snapshotBuilder = snapshotBuilder;
    }

    public static <TKey extends Comparable<TKey>>
    AverageChart<TKey> create(
            @Nonnull Coefficients coefficients,
            @Nonnull ChartSnapshotBuilder<TKey> snapshotFactory,
            @Nonnull StorageChartGroup<TKey, Double> storageChartGroup,
            @Nonnull VerificationChartModule verificationChartModule,
            @Nonnull NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        checkOnNull(coefficients, "Coefficients");
        checkOnNull(snapshotFactory, "Snapshot factory");

        return new AverageChart<>(coefficients, snapshotFactory, storageChartGroup,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, Double>> controlGroups) {
        checkOnNull(controlGroups, "Control groups");

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
    protected void checkGroups(List<ChartControlGroup<TKey, Double>> controlGroups) throws ControlChartException {
        int groupSize = controlGroups.get(0).size();
        for (ChartControlGroup<TKey, Double> group : controlGroups)
            if (group.size() != groupSize)
                throw new ControlChartException("Incorrect control group format!");
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, Double>> groups) {
        List<Measurement<TKey, Double>> resultValues = new LinkedList<>();
        for (ChartControlGroup<TKey, Double> group : groups) {
            resultValues.add(Measurement.create(group.getKey(), calculateAverage(group.values())));
        }

        return resultValues;
    }

    @Override
    protected double calculateCentralLine(@Nonnull List<Measurement<TKey, Double>> values) {
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
            List<ChartControlGroup<TKey, Double>> controlGroups,
            double tabularCoefficient
    ) {
        double averageRange = calculateAverage(controlGroups
                        .stream()
                        .map(group -> calculateRange(group
                                .getAll()
                                .values()
                                .stream()
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
