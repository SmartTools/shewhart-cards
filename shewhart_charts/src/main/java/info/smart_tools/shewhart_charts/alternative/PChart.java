package info.smart_tools.shewhart_charts.alternative;

import info.smart_tools.shewhart_charts.ControlChartException;
import info.smart_tools.shewhart_charts.SmartControlChart;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.utils.GroupFieldHelper;
import info.smart_tools.shewhart_charts.utils.Measurement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

public class PChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<Double, TKey> {

    private int controlledNumber;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private PChart(
            int controlledNumber,
            StorageModule<TKey, Double> storageModule,
            VerificationModule verificationModule,
            NotificationModule notificationModule
    ) throws IllegalArgumentException {
        super(storageModule, verificationModule, notificationModule);
        this.controlledNumber = controlledNumber;
        this.snapshotBuilder = GeneralChartSnapshotBuilder.create();
    }

    public static <TKey extends Comparable<TKey>>
    PChart<TKey> create(
            int controlledNumber,
            @Nonnull StorageModule<TKey, Double> storageModule,
            @Nonnull VerificationModule verificationModule,
            @Nonnull NotificationModule notificationModule
    ) throws IllegalArgumentException {
        return new PChart<>(controlledNumber, storageModule,
                verificationModule, notificationModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, Double>> controlGroups)
            throws ControlChartException{

        checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        List<Measurement<TKey, Double>> incorrectValues = getIncorrectFrom(controlGroups);
        Double centralLine = calculateCentralLine(incorrectValues);
        Double upperCentralLine = calculateUpperCentralLine(centralLine, controlledNumber);
        Double lowerCentralLine = calculateLowerCentralLine(centralLine, controlledNumber);

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
            if (!group.getAll().containsKey(GroupFieldHelper.ALT_CHART_INCORRECT_NUMBER)
                    || !group.getAll().containsKey(GroupFieldHelper.ALT_CHART_CONTROL_NUMBER)
                    || !group.getAll().get(GroupFieldHelper.ALT_CHART_CONTROL_NUMBER).equals((double) controlledNumber))
                throw new ControlChartException("Incorrect control group format!");
    }

    @Override
    protected List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, Double>> controlGroups) {
        List<Measurement<TKey, Double>> measurements = controlGroups
                .stream()
                .map(group -> Measurement.create(
                        group.getKey(),
                        group.get(GroupFieldHelper.ALT_CHART_INCORRECT_NUMBER) / controlledNumber))
                .collect(Collectors.toList());
        Collections.sort(measurements);

        return measurements;
    }

    @Override
    protected double calculateCentralLine(List<Measurement<TKey, Double>> values) {
        double sumIncorrect = 0;
        for (Measurement<TKey, Double> value : values) {
            sumIncorrect += value.getValue();
        }

        return sumIncorrect / (controlledNumber * values.size());
    }

    @Override
    protected double calculateUpperCentralLine(double centralLine, double coefficient) {
        return centralLine + 3 * Math.sqrt(centralLine * (1 - centralLine) / coefficient);
    }

    @Override
    protected double calculateLowerCentralLine(double centralLine, double coefficient) {
        double line =  centralLine - 3 * Math.sqrt(centralLine * (1 - centralLine) / coefficient);
        return line < 0 ? 0 : line;
    }

    private List<Measurement<TKey, Double>> getIncorrectFrom(List<ChartControlGroup<TKey, Double>> controlGroups) {
        return controlGroups
                .stream()
                .map(group -> Measurement.create(
                        group.getKey(),
                        group.get(GroupFieldHelper.ALT_CHART_INCORRECT_NUMBER)))
                .collect(Collectors.toList());
    }

}
