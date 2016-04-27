package org.maminsibirac.shewhart.groups;

public interface ControlGroup<TKey extends Comparable<TKey>> {
    TKey getKey();
}
