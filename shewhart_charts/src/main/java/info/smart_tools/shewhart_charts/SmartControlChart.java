package info.smart_tools.shewhart_charts;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.sensor.Sensor;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.utils.Measurement;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

public abstract class SmartControlChart<TValue extends Number, TKey extends Comparable<TKey>>
        implements ControlChart<TValue, TKey>, Sensor<List<ChartControlGroup<TKey, TValue>>> {

    private StorageModule<TKey, TValue> storageModule;
    private VerificationModule verificationModule;
    private NotificationModule notificationModule;

    protected SmartControlChart(
            @Nonnull StorageModule<TKey, TValue> storageModule,
            @Nonnull VerificationModule verificationModule,
            @Nonnull NotificationModule notificationModule
    ) throws IllegalArgumentException {
        checkChartModules(storageModule, verificationModule, notificationModule);
        this.storageModule = storageModule;
        this.verificationModule = verificationModule;
        this.notificationModule = notificationModule;
    }

    @Override
    public List<SpecialReason<TKey>> verify(@Nonnull ChartSnapshot<TKey> chartSnapshot) throws ControlChartException {
        checkOnNull(chartSnapshot, "Chart snapshot");
        List<SpecialReason<TKey>> errors = new LinkedList<>();
        verificationModule.verify(chartSnapshot, errors);

        return errors;
    }

    @Override
    public void report() throws ControlChartException {
        ChartSnapshot<TKey> snapshot = doSnapshot();
        List<SpecialReason<TKey>> errors = this.verify(snapshot);
        notificationModule.notify(snapshot, errors);
    }

    public ChartSnapshot<TKey> doSnapshot() throws ControlChartException {
        List<ChartControlGroup<TKey, TValue>> controlGroups = storageModule.getAll();
        return doSnapshot(controlGroups);
    }

    public ChartSnapshot<TKey> doSnapshot(@Nonnull TKey beginKey) throws ControlChartException {
        checkOnNull(beginKey, "Begin key");
        List<ChartControlGroup<TKey, TValue>> controlGroups = storageModule.get(beginKey);

        return doSnapshot(controlGroups);
    }

    public ChartSnapshot<TKey> doSnapshot(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws ControlChartException {
        checkOnNull(beginKey, "Begin key");
        checkOnNull(endKey, "End key");
        List<ChartControlGroup<TKey, TValue>> controlGroups = storageModule.get(beginKey, endKey);

        return doSnapshot(controlGroups);
    }

    public void update(@Nonnull List<ChartControlGroup<TKey, TValue>> controlGroups) throws ControlChartException {
        checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);
        storageModule.save(controlGroups);
        this.report();
    }

    public void forget(@Nonnull TKey key) throws ControlChartException {
        storageModule.remove(key);
    }

    public void forget(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws ControlChartException {
        storageModule.remove(beginKey, endKey);
    }

    public void forgetAll() throws ControlChartException {
        storageModule.removeAll();
    }

    protected abstract List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, TValue>> groups);

    protected abstract double calculateCentralLine(List<Measurement<TKey, Double>> values);

    protected abstract double calculateUpperCentralLine(double centralLine, double coefficient);

    protected abstract double calculateLowerCentralLine(double centralLine, double coefficient);

    protected abstract void checkGroups(List<ChartControlGroup<TKey, TValue>> controlGroups) throws ControlChartException;

    private void checkChartModules(
            StorageModule storageModule,
            VerificationModule verificationModule,
            NotificationModule notificationModule
    ) throws IllegalArgumentException {
        checkOnNull(storageModule, "Storage module");
        checkOnNull(verificationModule, "Verification module");
        checkOnNull(notificationModule, "Notification module");
    }

}
