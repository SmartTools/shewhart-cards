package org.maminsibirac.shewhart.charts;

import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.groups.ControlGroup;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;

import java.util.List;

public interface ControlChart<TKey extends Comparable<TKey>, Group extends ControlGroup<TKey>> {
    ChartSnapshot<TKey> doSnapshot(List<Group> groups);
    List<SpecialReason<TKey>> verify(ChartSnapshot<TKey> chartSnapshot);
}
