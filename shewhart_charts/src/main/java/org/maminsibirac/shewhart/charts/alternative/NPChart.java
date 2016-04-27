package org.maminsibirac.shewhart.charts.alternative;

import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.SmartControlChart;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.groups.alternative.AlternativeValuesControlGroup;
import org.maminsibirac.shewhart.modules.notification.NotificationChartModule;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;

import java.util.List;
import java.util.stream.Collectors;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

public class NPChart<TKey extends Comparable<TKey>>
        extends SmartControlChart<TKey, AlternativeValuesControlGroup<TKey>> {

    private int controlledNumber;
    private ChartSnapshotBuilder<TKey> snapshotBuilder;

    private NPChart(
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
    NPChart<TKey> create(
            int controlledNumber,
            ChartSnapshotBuilder<TKey> snapshotFactory,
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        checkOnNull(snapshotFactory, "");
        return new NPChart<>(controlledNumber, snapshotFactory, storageChartModule,
                verificationChartModule, notificationChartModule);
    }

    @Override
    public ChartSnapshot<TKey> doSnapshot(List<AlternativeValuesControlGroup<TKey>> controlGroups) {
        checkOnNull(controlGroups, "");

        List<Measurement<TKey, Double>> values = calculateValues(controlGroups);
        Double centralLine = calculateCentralLine(values);
        Double coefficient = centralLine / controlledNumber;
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
    protected List<Measurement<TKey, Double>> calculateValues(List<AlternativeValuesControlGroup<TKey>> controlGroups) {
        return controlGroups
                .stream()
                .map(group -> Measurement.create(
                        group.getKey(), (double) group.getIncorrectNumber())
                )
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
        return centralLine + 3 * Math.sqrt(centralLine * (1 - coefficient));
    }

    @Override
    protected double calculateLowerCentralLine(double centralLine, double coefficient) {
        double line =  centralLine - 3 * Math.sqrt(centralLine * (1 - coefficient));
        return line < 0 ? 0 : line;
    }

}
