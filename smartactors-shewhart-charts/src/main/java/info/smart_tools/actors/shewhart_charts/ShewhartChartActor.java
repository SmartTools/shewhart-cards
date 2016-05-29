package info.smart_tools.actors.shewhart_charts;

import info.smart_tools.actors.shewhart_charts.wrappers.*;
import info.smart_tools.shewhart_charts.ControlChartException;
import info.smart_tools.shewhart_charts.SmartControlChart;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.smartactors.core.actor.Actor;
import info.smart_tools.smartactors.core.actor.MessageFormatException;
import info.smart_tools.smartactors.core.actor.MessageHandlingException;
import info.smart_tools.smartactors.core.actor.annotation.Handler;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ioc.IOC;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class ShewhartChartActor extends Actor {
    private SmartControlChart<Double, Long> controlChart;

    public ShewhartChartActor(ChartParams params) throws MessageHandlingException {
        try {
            StorageModule<Double, Long> storageModule = IOC.resolve(
                    IOC.resolve(
                            IOC.getKeyForKeyStorage(),
                            params.getStorageModule()
                    )
            );
            VerificationModule verificationModule = IOC.resolve(
                    IOC.resolve(
                            IOC.getKeyForKeyStorage(),
                            params.getVerificationModule()
                    )
            );
            NotificationModule notificationModule = IOC.resolve(
                    IOC.resolve(
                            IOC.getKeyForKeyStorage(),
                            params.getNotificationModule()
                    )
            );

            this.controlChart = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyStorage(), params.getChart()),
                    storageModule,
                    verificationModule,
                    notificationModule,
                    params.getOptionalParams().orElse(new HashMap<>())
            );
        } catch (ResolutionException e) {
            throw new MessageHandlingException("Creation of ShewhartChartActor is failed: " + e.getMessage(), e);
        }
    }

    @Handler("update")
    public void update(ChartUpdateMessage message) throws MessageFormatException, MessageHandlingException {
        try {
            List<ChartControlGroup<Long, Double>> controlGroups =
                    message.getControlGroups().get().collect(Collectors.toList());
            controlChart.update(controlGroups);
        } catch (ControlChartException e) {
            throw new MessageHandlingException("" + e.getMessage(), e);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new MessageFormatException("" + e.getMessage(), e);
        }
    }

    @Handler("verify")
    public void verify(ChartVerificationMessage message) throws MessageFormatException, MessageHandlingException {
        try {
            message.setSpecialReasons(controlChart.verify(message.getChartSnapshot().get()));
        } catch (ControlChartException e) {
            throw new MessageHandlingException("" + e.getMessage(), e);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new MessageFormatException("" + e.getMessage(), e);
        }
    }

    @Handler("report")
    public void report(ChartReportMessage message) throws MessageHandlingException {
        try {
            controlChart.report();
        } catch (ControlChartException e) {
            throw new MessageHandlingException("" + e.getMessage(), e);
        }
    }

    @Handler("doSnapshotByPeriod")
    public void doSnapshotByPeriod(ChartSnapshotMessage message) throws MessageHandlingException {
        try {
            Long beginKey = message.getBeginKey();
            Long endKey = message.getEndKey();
            if (beginKey != null)
                if (endKey != null)
                    message.setChartSnapshot(controlChart.doSnapshot(beginKey, endKey));
                else
                    message.setChartSnapshot(controlChart.doSnapshot(beginKey));
            else
                message.setChartSnapshot(controlChart.doSnapshot());
        } catch (ControlChartException e) {
            throw new MessageHandlingException("" + e.getMessage(), e);
        }
    }

    @Handler("doOneTimeSnapshot")
    public void doOneTimeSnapshot(ChartSnapshotMessage message)
            throws MessageFormatException, MessageHandlingException {
        try {
            List<ChartControlGroup<Long, Double>> controlGroups =
                    message.getControlGroups().get().collect(Collectors.toList());
            message.setChartSnapshot(controlChart.doSnapshot(controlGroups));
        } catch (ControlChartException e) {
            throw new MessageHandlingException("" + e.getMessage(), e);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new MessageFormatException("" + e.getMessage(), e);
        }
    }

    @Handler("forget")
    public void forget(ChartDataForgetMessage message) throws MessageHandlingException{
        try {
            Long beginKey = message.getBeginKey();
            Long endKey = message.getEndKey();
            if (beginKey != null)
                if (endKey != null)
                    controlChart.forget(beginKey, endKey);
                else
                    controlChart.forget(beginKey);
            else
                controlChart.forgetAll();
        } catch (ControlChartException e) {
            throw new MessageHandlingException("" + e.getMessage(), e);
        }
    }

}
