package info.smart_tools.shewhart_charts;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.CorruptedModuleException;
import info.smart_tools.shewhart_charts.modules.notification.NotificationException;
import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.storage.DeleteGroupsException;
import info.smart_tools.shewhart_charts.modules.storage.InsertGroupsException;
import info.smart_tools.shewhart_charts.modules.storage.SelectGroupsException;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;
import info.smart_tools.shewhart_charts.modules.verification.VerificationException;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.utils.Measurement;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

public abstract class SmartControlChart<TValue extends Number, TKey extends Comparable<TKey>>
        implements ControlChart<TValue, TKey> {

    private final String corruptedModuleMsg = "module is corrupted and should be replaced. Unhandled exception: ";

    private StorageModule<TKey, TValue> storageModule;
    private VerificationModule verificationModule;
    private NotificationModule notificationModule;

    private ChartSnapshot<TKey> lastFullSnapshot;
    private Boolean lastSnapshotStatus;

    protected SmartControlChart(
            @Nonnull StorageModule<TKey, TValue> storageModule,
            @Nonnull VerificationModule verificationModule,
            @Nonnull NotificationModule notificationModule
    ) throws IllegalArgumentException {
        checkChartModules(storageModule, verificationModule, notificationModule);
        this.storageModule = storageModule;
        this.verificationModule = verificationModule;
        this.notificationModule = notificationModule;

        this.lastFullSnapshot = null;
        this.lastSnapshotStatus = Boolean.FALSE;
    }

    @Override
    public List<SpecialReason<TKey>> verify(@Nonnull ChartSnapshot<TKey> chartSnapshot) throws ControlChartException {
        checkOnNull(chartSnapshot, "Chart snapshot");
        List<SpecialReason<TKey>> errors = new LinkedList<>();
        try {
            verificationModule.verify(chartSnapshot, errors);
        } catch (VerificationException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Verification" + corruptedModuleMsg + e.getMessage(), e);
        }

        return errors;
    }

    public void report() throws ControlChartException {
        ChartSnapshot<TKey> snapshot = doSnapshot();
        List<SpecialReason<TKey>> errors = this.verify(snapshot);
        try {
            notificationModule.notify(snapshot, errors);
        } catch (NotificationException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Notification" + corruptedModuleMsg + e.getMessage(), e);
        }
    }

    public ChartSnapshot<TKey> doSnapshot() throws ControlChartException {
        List<ChartControlGroup<TKey, TValue>> controlGroups;
        try {
            controlGroups = storageModule.selectAll();
        } catch (SelectGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }

        return lastFullSnapshot = lastSnapshotStatus ? lastFullSnapshot : doSnapshot(controlGroups);
    }

    public ChartSnapshot<TKey> doSnapshot(@Nonnull TKey beginKey) throws ControlChartException {
        checkOnNull(beginKey, "Begin key");
        List<ChartControlGroup<TKey, TValue>> controlGroups;
        try {
            controlGroups = storageModule.select(beginKey);
        } catch (SelectGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }

        return doSnapshot(controlGroups);
    }

    public ChartSnapshot<TKey> doSnapshot(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws ControlChartException {
        checkOnNull(beginKey, "Begin key");
        checkOnNull(endKey, "End key");
        List<ChartControlGroup<TKey, TValue>> controlGroups;
        try {
            controlGroups = storageModule.select(beginKey, endKey);
        } catch (SelectGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }

        return doSnapshot(controlGroups);
    }

    public SmartControlChart<TValue, TKey> update(@Nonnull List<ChartControlGroup<TKey, TValue>> controlGroups)
            throws ControlChartException {

        checkOnNull(controlGroups, "Control groups");
        checkGroups(controlGroups);
        try {
            storageModule.insert(controlGroups);
        } catch (InsertGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }

        return this;
    }

    public void remove(@Nonnull TKey key) throws ControlChartException {
        try {
            storageModule.delete(key);
        } catch (DeleteGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }
    }

    public void remove(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws ControlChartException {
        try {
            storageModule.delete(beginKey, endKey);
        } catch (DeleteGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }
    }

    public void removeAll() throws ControlChartException {
        try {
            storageModule.deleteAll();
        } catch (DeleteGroupsException e) {
            throw new ControlChartException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CorruptedModuleException("Storage" + corruptedModuleMsg + e.getMessage(), e);
        }
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
