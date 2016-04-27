package org.maminsibirac.shewhart.modules.notification;

import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;

import java.util.List;

public interface NotificationChartModule {
    <TKey extends Comparable<TKey>>
    void notify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> errors);
}
