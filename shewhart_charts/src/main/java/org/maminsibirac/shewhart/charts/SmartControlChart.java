package org.maminsibirac.shewhart.charts;

import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.groups.ControlGroup;
import org.maminsibirac.shewhart.modules.notification.NotificationChartModule;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;
import org.maminsibirac.shewhart.sensor.Sensor;
import org.maminsibirac.shewhart.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class SmartControlChart<TKey extends Comparable<TKey>, Group extends ControlGroup<TKey>>
        implements ControlChart<TKey, Group>, Sensor<List<Group>> {

    private StorageChartModule<TKey> storageChartModule;
    private VerificationChartModule verificationChartModule;
    private NotificationChartModule notificationChartModule;

    protected SmartControlChart(
            StorageChartModule<TKey> storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        checkChartModules(storageChartModule, verificationChartModule, notificationChartModule);
        this.storageChartModule = storageChartModule;
        this.verificationChartModule = verificationChartModule;
        this.notificationChartModule = notificationChartModule;
    }

    @Override
    public List<SpecialReason<TKey>> verify(ChartSnapshot<TKey> chartSnapshot) {
        List<SpecialReason<TKey>> errors = new ArrayList<>();
        verificationChartModule.verify(chartSnapshot, errors);
        return errors;
    }

    @Override
    public void report() {
        ChartSnapshot<TKey> snapshot = doSnapshot();
        List<SpecialReason<TKey>> errors = this.verify(snapshot);
        notificationChartModule.notify(snapshot, errors);
    }

    @Override
    public void run() {
        //ToDo : override.
    }

    @Override
    public void close() throws IOException {
        //ToDo : override.
    }

    public ChartSnapshot<TKey> doSnapshot() {
        List<Group> controlGroup = (List<Group>) storageChartModule.getAll();
        return doSnapshot(controlGroup);
    }

    public ChartSnapshot<TKey> doSnapshot(TKey beginKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key");
        List<Group> controlGroup = (List<Group>) storageChartModule.get(beginKey);

        return doSnapshot(controlGroup);
    }

    public ChartSnapshot<TKey> doSnapshot(TKey beginKey, TKey endKey) {
        ValidationUtils.checkOnNull(beginKey, "");
        ValidationUtils.checkOnNull(endKey, "");
        List<Group> controlGroup = (List<Group>) storageChartModule.get(beginKey, endKey);

        return doSnapshot(controlGroup);
    }

    public void update(List<Group> controlGroup) {
        ValidationUtils.checkOnNull(controlGroup, "Control groups");
        storageChartModule.save(controlGroup);
        this.report();
    }

    public void forget(TKey key) {
        storageChartModule.remove(key);
    }

    public void forget(TKey beginKey, TKey endKey) {
        storageChartModule.remove(beginKey, endKey);
    }

    public void forgetAll() {
        storageChartModule.removeAll();
    }

    protected abstract List<Measurement<TKey, Double>> calculateValues(List<Group> groups);

    protected abstract double calculateCentralLine(List<Measurement<TKey, Double>> values);

    protected abstract double calculateUpperCentralLine(double centralLine, double coefficient);

    protected abstract double calculateLowerCentralLine(double centralLine, double coefficient);

    private void checkChartModules(
            StorageChartModule storageChartModule,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(storageChartModule, "");
        ValidationUtils.checkOnNull(verificationChartModule, "");
        ValidationUtils.checkOnNull(notificationChartModule, "");
    }

}
