package info.smart_tools.shewhart_charts.modules.notification;

import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import java.util.List;

public interface NotificationModule {
    <TKey extends Comparable<TKey>>
    void notify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> errors);
}
