package info.smart_tools.actors.shewhart_charts.wrappers;

import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;

public interface ChartSnapshotMessage extends ChartUpdateMessage {
    Long getBeginKey();
    Long getEndKey();

    void setBeginKey(Long beginKey);
    void setEndKey(Long key);
    void setChartSnapshot(ChartSnapshot<Long> snapshot);
}
