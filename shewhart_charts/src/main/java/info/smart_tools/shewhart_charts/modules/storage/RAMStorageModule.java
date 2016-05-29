package info.smart_tools.shewhart_charts.modules.storage;

import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.*;

public class RAMStorageModule<TKey extends Comparable<TKey>, TValue extends Number>
        implements StorageModule<TKey, TValue> {

    private TreeMap<TKey, ChartControlGroup<TKey, TValue>> storage;

    protected RAMStorageModule() {
        this.storage = new TreeMap<>();
    }

    public static
    <TKey extends Comparable<TKey>, TValue extends Number> RAMStorageModule<TKey, TValue>
    create() {
        return new RAMStorageModule<>();
    }

    @Override
    public void save(@Nonnull List<ChartControlGroup<TKey, TValue>> groups) {
        ValidationUtils.checkOnNullOrEmpty(groups, "Groups");
        for (ChartControlGroup<TKey, TValue> group : groups)
            storage.put(group.getKey(), group);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public List<ChartControlGroup<TKey, TValue>> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<ChartControlGroup<TKey, TValue>> get(@Nonnull TKey beginKey, @Nonnull TKey endKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key");
        ValidationUtils.checkOnNull(endKey, "End key");

        List<ChartControlGroup<TKey, TValue>> searchResult = new LinkedList<>();
        List<ChartControlGroup<TKey, TValue>> values = new ArrayList<>(storage.values());
        int storageSize = storage.size();
        for (int i = 0; i < storageSize; ++i) {
            ChartControlGroup<TKey, TValue> currentGroup = values.get(i);
            if (currentGroup.getKey().compareTo(beginKey) >= 0 && currentGroup.getKey().compareTo(endKey) <= 0)
                searchResult.add(currentGroup);
        }

        return searchResult;
    }

    @Override
    public List<ChartControlGroup<TKey, TValue>> get(@Nonnull TKey beginKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key");

        List<ChartControlGroup<TKey, TValue>> searchResult = new LinkedList<>();
        List<ChartControlGroup<TKey, TValue>> values = new ArrayList<>(storage.values());
        int beginIndex = values.indexOf(storage.get(beginKey));
        if (beginIndex == -1)
            return new ArrayList<>(1);

        int storageSize = storage.size();
        for (int i = beginIndex; i < storageSize; ++i) {
            ChartControlGroup<TKey, TValue> currentGroup = values.get(i);
            if (currentGroup.getKey().compareTo(beginKey) >= 0)
                searchResult.add(currentGroup);
        }

        return searchResult;
    }

    @Override
    public void remove(@Nonnull TKey key) {
        ValidationUtils.checkOnNull(key, "Key");
        storage.remove(key);
    }

    @Override
    public void remove(@Nonnull TKey beginKey, @Nonnull TKey endKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key");
        ValidationUtils.checkOnNull(endKey, "End key");

        List<ChartControlGroup<TKey, TValue>> values = new ArrayList<>(storage.values());
        int storageSize = storage.size();
        for (int i = 0; i < storageSize; ++i) {
            TKey currentKey = values.get(i).getKey();
            if (currentKey.compareTo(beginKey) >= 0 && currentKey.compareTo(endKey) <= 0)
                storage.remove(currentKey);
        }
    }

    @Override
    public void removeAll() {
        storage = new TreeMap<>();
    }
}
