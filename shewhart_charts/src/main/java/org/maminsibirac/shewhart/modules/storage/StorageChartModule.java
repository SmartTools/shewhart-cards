package org.maminsibirac.shewhart.modules.storage;

import org.maminsibirac.shewhart.groups.ControlGroup;

import java.util.List;

public interface StorageChartModule<TKey extends Comparable<TKey>> {
    void save(List<? extends ControlGroup<TKey>> group);
    int size();

    List<? extends ControlGroup<TKey>> getAll();
    List<? extends ControlGroup<TKey>> get(TKey beginKey, TKey endKey);
    List<? extends ControlGroup<TKey>> get(TKey beginKey);

    void remove(TKey key);
    void remove(TKey beginKey, TKey endKey);
    void removeAll();
}
