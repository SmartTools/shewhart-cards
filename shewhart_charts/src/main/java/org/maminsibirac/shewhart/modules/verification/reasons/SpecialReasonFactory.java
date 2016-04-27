package org.maminsibirac.shewhart.modules.verification.reasons;

import org.maminsibirac.shewhart.charts.Measurement;

import java.util.List;

public interface SpecialReasonFactory {
    <TKey extends Comparable<TKey>> SpecialReason<TKey>
    create(String name, List<Measurement<TKey, Double>> keys);
}
