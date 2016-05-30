package info.smart_tools.shewhart_charts.quantitative;

import info.smart_tools.shewhart_charts.ControlChartException;
import info.smart_tools.shewhart_charts.SmartControlChart;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.utils.Coefficients;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RangeChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<Double, TKey> {

    private Coefficients coefficients;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private RangeChart(
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
    RangeChart<TKey> create(
            @Nonnull Coefficients coefficients,
            @Nonnull StorageModule<TKey, Double> storageModule,
            @Nonnull VerificationModule verificationModule,
            @Nonnull NotificationModule notificationModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(coefficients, "Coefficients");
        return new RangeChart<>(coefficients, storageModule,
                verificationModule, notificationModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, Double>> controlGroups)
            throws ControlChartException{

        ValidationUtils.checkOnNull(controlGroups, "");
        checkGroups(controlGroups);

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        Double centralLine = calculateCentralLine(values);
        int combination = controlGroups.get(0).size();
        Double upperCentralLine = calculateUpperCentralLine(centralLine,
                coefficients.get(combination, "D4"));
        Double lowerCentralLine = calculateLowerCentralLine(centralLine,
                coefficients.get(combination, "D3"));

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
    protected List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, Double>> controlGroups) {
        List<Measurement<TKey, Double>> measurements = controlGroups
                .stream()
                .map(group -> Measurement.create(group.getKey(), calculateRange(group.values())))
                .collect(Collectors.toList());
        Collections.sort(measurements);

        return measurements;
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
        return centralLine * coefficient;
    }

    @Override
    protected double calculateLowerCentralLine(double centralLine, double coefficient) {
        return centralLine * coefficient;
    }

    private Double calculateRange(List<Double> values) {
        Double max = Collections.max(values);
        Double min = Collections.min(values);

        return max - min;
    }

    double calculateAverage(List<Double> values) {
        Double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }

        return sum / values.size();
    }

}
