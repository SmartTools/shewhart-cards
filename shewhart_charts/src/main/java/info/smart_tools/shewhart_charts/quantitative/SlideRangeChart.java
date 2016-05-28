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
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SlideRangeChart <TKey extends Comparable<TKey>>
        extends SmartControlChart<Double, TKey> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private SlideRangeChart(
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
    SlideRangeChart<TKey> create(
            Coefficients coefficients,
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartGroup<TKey, Double> storageChartGroup,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(coefficients, "Coefficients");
        ValidationUtils.checkOnNull(snapshotFactory, "Snapshot factory");

        return new SlideRangeChart<>(coefficients, snapshotFactory, storageChartGroup,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, Double>> controlGroups)
            throws ControlChartException{

        ValidationUtils.checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);

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
    protected void checkGroups(List<ChartControlGroup<TKey, Double>> controlGroups) throws ControlChartException {
        for (ChartControlGroup<TKey, Double> group : controlGroups)
            if (group.size() != 1)
                throw new ControlChartException("Incorrect control group format!");
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, Double>> controlGroups) {
        return calculateRanges(controlGroups
                .stream()
                .map(group -> Measurement.create(group.getKey(), group
                        .values()
                        .get(0)))
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
