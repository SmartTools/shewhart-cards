package info.smart_tools.shewhart_charts;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationChartModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageChartGroup;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.sensor.Sensor;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class SmartControlChart<TValue extends Number, TKey extends Comparable<TKey>>
        implements ControlChart<TValue, TKey>, Sensor<List<ChartControlGroup<TKey, TValue>>> {

    private StorageChartGroup<TKey, TValue> storageChartGroup;
    private VerificationChartModule verificationChartModule;
    private NotificationChartModule notificationChartModule;

    protected SmartControlChart(
            @Nonnull StorageChartGroup<TKey, TValue> storageChartGroup,
            @Nonnull VerificationChartModule verificationChartModule,
            @Nonnull NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        checkChartModules(storageChartGroup, verificationChartModule, notificationChartModule);
        this.storageChartGroup = storageChartGroup;
        this.verificationChartModule = verificationChartModule;
        this.notificationChartModule = notificationChartModule;
    }

    @Override
    public List<SpecialReason<TKey>> verify(@Nonnull ChartSnapshot<TKey> chartSnapshot) throws ControlChartException {
        List<SpecialReason<TKey>> errors = new ArrayList<>();
        verificationChartModule.verify(chartSnapshot, errors);
        return errors;
    }

    @Override
    public void report() throws ControlChartException {
        ChartSnapshot<TKey> snapshot = doSnapshot();
        List<SpecialReason<TKey>> errors = this.verify(snapshot);
        notificationChartModule.notify(snapshot, errors);
    }

    public ChartSnapshot<TKey> doSnapshot() throws ControlChartException {
        List<ChartControlGroup<TKey, TValue>> controlGroups = storageChartGroup.getAll();
        return doSnapshot(controlGroups);
    }

    public ChartSnapshot<TKey> doSnapshot(@Nonnull TKey beginKey) throws ControlChartException {
        ValidationUtils.checkOnNull(beginKey, "Begin key");
        List<ChartControlGroup<TKey, TValue>> controlGroups = storageChartGroup.get(beginKey);

        return doSnapshot(controlGroups);
    }

    public ChartSnapshot<TKey> doSnapshot(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws ControlChartException {
        ValidationUtils.checkOnNull(beginKey, "Begin key");
        ValidationUtils.checkOnNull(endKey, "End key");
        List<ChartControlGroup<TKey, TValue>> controlGroups = storageChartGroup.get(beginKey, endKey);

        return doSnapshot(controlGroups);
    }

    public void update(@Nonnull List<ChartControlGroup<TKey, TValue>> controlGroups) throws ControlChartException {
        ValidationUtils.checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);
        storageChartGroup.save(controlGroups);
        this.report();
    }

    public void forget(@Nonnull TKey key) throws ControlChartException {
        storageChartGroup.remove(key);
    }

    public void forget(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws ControlChartException {
        storageChartGroup.remove(beginKey, endKey);
    }

    public void forgetAll() throws ControlChartException {
        storageChartGroup.removeAll();
    }

    protected abstract List<Measurement<TKey, Double>> calculateValues(List<ChartControlGroup<TKey, TValue>> groups);

    protected abstract double calculateCentralLine(List<Measurement<TKey, Double>> values);

    protected abstract double calculateUpperCentralLine(double centralLine, double coefficient);

    protected abstract double calculateLowerCentralLine(double centralLine, double coefficient);

    protected abstract void checkGroups(List<ChartControlGroup<TKey, TValue>> controlGroups) throws ControlChartException;

    private void checkChartModules(
            StorageChartGroup storageChartGroup,
            VerificationChartModule verificationChartModule,
            NotificationChartModule notificationChartModule
    ) throws IllegalArgumentException {
        ValidationUtils.checkOnNull(storageChartGroup, "Storage module");
        ValidationUtils.checkOnNull(verificationChartModule, "Verification module");
        ValidationUtils.checkOnNull(notificationChartModule, "Notification module");
    }

}
