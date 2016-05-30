package info.smart_tools.shewhart_charts.modules.storage;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;

import javax.annotation.Nonnull;
import java.util.List;

public interface StorageModule<TKey extends Comparable<TKey>, TValue extends Number> {
    void insert(@Nonnull List<ChartControlGroup<TKey, TValue>> groups) throws InsertGroupsException;
    int size();

    List<ChartControlGroup<TKey, TValue>> selectAll() throws SelectGroupsException;
    List<ChartControlGroup<TKey, TValue>> select(@Nonnull TKey beginKey, @Nonnull TKey endKey)
            throws SelectGroupsException;
    List<ChartControlGroup<TKey, TValue>> select(TKey beginKey) throws SelectGroupsException;

    void delete(@Nonnull TKey key) throws DeleteGroupsException;
    void delete(@Nonnull TKey beginKey, @Nonnull TKey endKey) throws DeleteGroupsException;
    void deleteAll() throws DeleteGroupsException;
}
