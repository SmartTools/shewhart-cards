package org.maminsibirac.shewhart.groups.quantitative;

import org.maminsibirac.shewhart.groups.ControlGroup;

public interface ISRChartsControlGroup<TValue extends Number, TKey extends Comparable<TKey>> extends ControlGroup<TKey> {
    void add(TValue value);
    TValue get();
}
