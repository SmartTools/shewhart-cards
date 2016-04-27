package org.maminsibirac.shewhart.charts.alternative;

import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.SmartControlChart;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.groups.alternative.AlternativeValuesControlGroup;
import org.maminsibirac.shewhart.modules.notification.NotificationChartModule;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.utils.ValidationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<TKey, AlternativeValuesControlGroup<TKey>> {

    private int controlledNumber;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private CChart(
            int controlledNumber,
            ChartSnapshotBuilder<TKey> snapshotBuilder,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        super(storageChartModule, verificationChartModule, notificationChartModule);
        this.controlledNumber = controlledNumber;
        this.snapshotBuilder = snapshotBuilder;
    }

    public static <TKey extends Comparable<TKey>>
    CChart<TKey> create(
            int controlledNumber,
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(snapshotFactory, "");

        return new CChart<>(controlledNumber, snapshotFactory, storageChartModule,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(List<AlternativeValuesControlGroup<TKey>> controlGroups) {
        ValidationUtils.checkOnNull(controlGroups, "");

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
    protected List<Measurement<TKey, Double>> calculateValues(List<AlternativeValuesControlGroup<TKey>> controlGroups) {
        return controlGroups
                .stream()
                .map(group -> Measurement.create(group.getKey(), (double) group.getIncorrectNumber()))
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
