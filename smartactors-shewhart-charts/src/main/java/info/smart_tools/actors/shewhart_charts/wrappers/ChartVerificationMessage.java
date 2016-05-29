package info.smart_tools.actors.shewhart_charts.wrappers;

import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.smartactors.core.iobject.IObjectWrapper;

import java.util.List;
import java.util.Optional;

public interface ChartVerificationMessage extends IObjectWrapper {
    Optional<ChartSnapshot<Long>> getChartSnapshot();
    void setChartSnapshot(ChartSnapshot<Long> snapshot);
    void setSpecialReasons(List<SpecialReason<Long>> reasons);
}
