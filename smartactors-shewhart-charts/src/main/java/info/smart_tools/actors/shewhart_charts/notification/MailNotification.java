package info.smart_tools.actors.shewhart_charts.notification;

import info.smart_tools.shewhart_charts.modules.notification.NotificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;

import java.util.List;

public class MailNotification implements NotificationModule {
    @Override
    public <TKey extends Comparable<TKey>>
    void notify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons) {

    }
}
