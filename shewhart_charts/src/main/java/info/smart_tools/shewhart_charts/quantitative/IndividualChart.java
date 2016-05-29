package info.smart_tools.shewhart_charts.quantitative;

import info.smart_tools.shewhart_charts.ControlChartException;
import info.smart_tools.shewhart_charts.SmartControlChart;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.utils.Coefficients;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IndividualChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<Double, TKey> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private IndividualChart(
            Coefficients coefficients,
            StorageModule<TKey, Double> storageModule,
            VerificationModule verificationModule,
            NotificationModule notificationModule
    ) throws IllegalArgumentException {
        super(storageModule, verificationModule, notificationModule);
        this.coefficients = coefficients;
        this.snapshotBuilder = GeneralChartSnapshotBuilder.create();
    }

    public static <TKey extends Comparable<TKey>>
    IndividualChart<TKey> create(
            @Nonnull Coefficients coefficients,
            @Nonnull StorageModule<TKey, Double> storageModule,
            @Nonnull VerificationModule verificationModule,
            @Nonnull NotificationModule notificationModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(coefficients, "Coefficients");
        return new IndividualChart<>(coefficients, storageModule,
                verificationModule, notificationModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, Double>> controlGroups)
            throws ControlChartException {

        ValidationUtils.checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);

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
    protected void checkGroups(List<ChartControlGroup<TKey, Double>> controlGroups) throws ControlChartException {
        for (ChartControlGroup<TKey, Double> group : controlGroups)
            if (group.size() != 1)
                throw new ControlChartException("Incorrect control group format!");
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, Double>> controlGroups) {
        return controlGroups
                .stream()
                .map(group -> Measurement.create(group.getKey(), group
                        .values()
                        .get(0)))
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
            List<ChartControlGroup<TKey, Double>> controlGroups,
            double tabularCoefficient
    ) {
        double averageRange = calculateAverage(
                calculateRanges(controlGroups
                                .stream()
                                .map(group -> group
                                        .values()
                                        .get(0))
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
