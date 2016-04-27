package org.maminsibirac.shewhart.groups.alternative;

import org.maminsibirac.shewhart.groups.ControlGroup;

public interface AlternativeValuesControlGroup<TKey extends Comparable<TKey>> extends ControlGroup<TKey> {
    void addIncorrectNumber(int incorrectNumber);
    int getIncorrectNumber();
}
