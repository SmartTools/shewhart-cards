package org.maminsibirac.shewhart.modules.verification;

import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;

import java.util.List;

public interface VerificationChartModule {
    <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons);
}
