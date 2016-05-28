package info.smart_tools.shewhart_charts.alternative;

import info.smart_tools.shewhart_charts.ControlChartException;
import info.smart_tools.shewhart_charts.SmartControlChart;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationChartModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageChartGroup;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.utils.GroupFieldHelper;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class CChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<Double, TKey> {

    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private CChart(
            ChartSnapshotBuilder<TKey> snapshotBuilder,
            StorageChartGroup<TKey, Double> storageChartGroup,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        super(storageChartGroup, verificationChartModule, notificationChartModule);
        this.snapshotBuilder = snapshotBuilder;
    }

    public static <TKey extends Comparable<TKey>>
    CChart<TKey> create(
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartGroup<TKey, Double> storageChartGroup,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(snapshotFactory, "Snapshot factory");

        return new CChart<>(snapshotFactory, storageChartGroup,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, Double>> controlGroups)
            throws ControlChartException{

        ValidationUtils.checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        Double centralLine = calculateCentralLine(values);
        Double upperCentralLine = calculateUpperCentralLine(centralLine, 0);
        Double lowerCentralLine = calculateLowerCentralLine(centralLine, 0);

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
            if (group.size() != 1 || !group.getAll().containsKey(GroupFieldHelper.ALT_CHART_INCORRECT_NUMBER))
                throw new ControlChartException("Incorrect control group format!");
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, Double>> controlGroups) {
        return controlGroups
                .stream()
                .map(group -> Measurement.create(
                        group.getKey(),
                        group.get(GroupFieldHelper.ALT_CHART_INCORRECT_NUMBER)))
                .collect(Collectors.toList());
    }

    @Override
    protected double calculateCentralLine(List<Measurement<TKey, Double>> values) {
        double sumIncorrect = 0;
        for (Measurement<TKey, Double> value : values) {
            sumIncorrect += value.getValue();
        }

        return sumIncorrect / values.size();
    }

    @Override
    protected double calculateUpperCentralLine(double centralLine, double coefficient) {
        return centralLine + 3 * Math.sqrt(centralLine);
    }

    @Override
    protected double calculateLowerCentralLine(double centralLine, double coefficient) {
        double line = centralLine - 3 * Math.sqrt(centralLine);
        return line < 0 ? 0 : line;
    }
}
