package info.smart_tools.shewhart_charts.modules.verification;

import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import javax.annotation.Nonnull;
import java.util.List;

public interface VerificationChartModule {
    <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> reasons);
}
