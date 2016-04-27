package org.maminsibirac.shewhart.modules.verification.reasons;

import org.maminsibirac.shewhart.charts.Measurement;

import java.util.List;

public interface SpecialReason<TKey extends Comparable<TKey>> {
    String getName();
    String getMessage();
    List<Measurement<TKey, Double>> getPointKeys();
}
