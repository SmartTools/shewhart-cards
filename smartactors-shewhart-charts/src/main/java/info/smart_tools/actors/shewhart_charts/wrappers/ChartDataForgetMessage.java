package info.smart_tools.actors.shewhart_charts.wrappers;

import info.smart_tools.smartactors.core.iobject.IObjectWrapper;

public interface ChartDataForgetMessage extends IObjectWrapper {
    Long getBeginKey();
    Long getEndKey();

    void setBeginKey(Long beginKey);
    void setEndKey(Long key);
}
