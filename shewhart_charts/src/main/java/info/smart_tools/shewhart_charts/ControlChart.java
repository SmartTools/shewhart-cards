package info.smart_tools.shewhart_charts;

import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import javax.annotation.Nonnull;
import java.util.List;

public interface ControlChart<TValue extends Number, TKey extends Comparable<TKey>> {
    ChartSnapshot<TKey> doSnapshot(@Nonnull List<ChartControlGroup<TKey, TValue>> groups) throws ControlChartException;
    List<SpecialReason<TKey>> verify(@Nonnull ChartSnapshot<TKey> chartSnapshot) throws ControlChartException;
}
