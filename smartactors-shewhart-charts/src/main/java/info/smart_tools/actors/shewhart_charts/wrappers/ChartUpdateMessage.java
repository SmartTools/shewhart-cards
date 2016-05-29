package info.smart_tools.actors.shewhart_charts.wrappers;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.smartactors.core.iobject.IObjectWrapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ChartUpdateMessage extends IObjectWrapper {
    Optional<Stream<ChartControlGroup<Long, Double>>> getControlGroups();
    void setControlGroup(List<ChartControlGroup<Long, Double>> controlGroups);
}
