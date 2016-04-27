package org.maminsibirac.shewhart.groups.quantitative;

import org.maminsibirac.shewhart.groups.ControlGroup;

import java.util.List;

public interface ARChartsControlGroup<TValue extends Number, TKey extends Comparable<TKey>> extends ControlGroup<TKey> {
    void add(String key, TValue value);
    TValue get(String key);
    int size();
    List<TValue> values();
}
