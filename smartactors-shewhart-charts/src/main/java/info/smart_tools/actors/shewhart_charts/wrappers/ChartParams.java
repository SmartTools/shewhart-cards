package info.smart_tools.actors.shewhart_charts.wrappers;

import info.smart_tools.smartactors.core.iobject.IObjectWrapper;

import java.util.Map;
import java.util.Optional;

public interface ChartParams extends IObjectWrapper {
    String getChart();
    String getStorageModule();
    String getVerificationModule();
    String getNotificationModule();
    Optional<Map<String, String>> getOptionalParams();
}
