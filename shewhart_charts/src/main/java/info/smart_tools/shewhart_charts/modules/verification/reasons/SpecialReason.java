package info.smart_tools.shewhart_charts.modules.verification.reasons;

import info.smart_tools.shewhart_charts.utils.Measurement;

import java.util.List;

public interface SpecialReason<TKey extends Comparable<TKey>> {
    String getName();
    String getMessage();
    List<Measurement<TKey, Double>> getPointKeys();
}
