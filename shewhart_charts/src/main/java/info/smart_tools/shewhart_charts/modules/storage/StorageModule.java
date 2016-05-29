package info.smart_tools.shewhart_charts.modules.storage;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;

import javax.annotation.Nonnull;
import java.util.List;

public interface StorageModule<TKey extends Comparable<TKey>, TValue extends Number> {
    void save(@Nonnull List<ChartControlGroup<TKey, TValue>> groups);
    int size();

    List<ChartControlGroup<TKey, TValue>> getAll();
    List<ChartControlGroup<TKey, TValue>> get(@Nonnull TKey beginKey, @Nonnull TKey endKey);
    List<ChartControlGroup<TKey, TValue>> get(TKey beginKey);

    void remove(@Nonnull TKey key);
    void remove(@Nonnull TKey beginKey, @Nonnull TKey endKey);
    void removeAll();
}
