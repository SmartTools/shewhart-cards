package org.maminsibirac.shewhart.modules.storage;

import org.maminsibirac.shewhart.groups.ControlGroup;
import org.maminsibirac.shewhart.utils.ValidationUtils;

import java.util.*;

// ToDo : add validation incoming values and handling exceptions.
public class RAMStorage<TKey extends Comparable<TKey>> implements StorageChartModule<TKey> {
    private TreeMap<TKey, ControlGroup<TKey>> storage;

    protected RAMStorage() {
        this.storage = new TreeMap<>();
    }

    public static <TKey extends Comparable<TKey>> RAMStorage<TKey> create() {
        return new RAMStorage<>();
    }

    @Override
    public void save(List<? extends ControlGroup<TKey>> groups) {
        ValidationUtils.checkOnNullOrEmpty(groups, "Groups for saving " + ValidationUtils.NULL_OR_EMPTY_ERROR_MSG);
        for (ControlGroup<TKey> group : groups) {
            storage.put(group.getKey(), group);
        }
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public List<? extends ControlGroup<TKey>> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<? extends ControlGroup<TKey>> get(TKey beginKey, TKey endKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key " + ValidationUtils.NULL_ERROR_MSG);
        ValidationUtils.checkOnNull(endKey, "End key " + ValidationUtils.NULL_ERROR_MSG);

        List<ControlGroup<TKey>> searchResult = new LinkedList<>();
        List<ControlGroup<TKey>> values = new ArrayList<>(storage.values());
        int storageSize = storage.size();
        for (int i = 0; i < storageSize; ++i) {
            ControlGroup<TKey> currentGroup = values.get(i);
            if (currentGroup.getKey().compareTo(beginKey) >= 0 && currentGroup.getKey().compareTo(endKey) <= 0)
                searchResult.add(currentGroup);
        }

        return searchResult;
    }

    @Override
    public List<? extends ControlGroup<TKey>> get(TKey beginKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key " + ValidationUtils.NULL_ERROR_MSG);

        List<ControlGroup<TKey>> searchResult = new LinkedList<>();
        List<ControlGroup<TKey>> values = new ArrayList<>(storage.values());
        int beginIndex = values.indexOf(storage.get(beginKey));
        if (beginIndex == -1)
            return new ArrayList<>(1);

        int storageSize = storage.size();
        for (int i = beginIndex; i < storageSize; ++i) {
            ControlGroup<TKey> currentGroup = values.get(i);
            if (currentGroup.getKey().compareTo(beginKey) >= 0)
                searchResult.add(currentGroup);
        }

        return searchResult;
    }

    @Override
    public void remove(TKey key) {
        ValidationUtils.checkOnNull(key, "Key " + ValidationUtils.NULL_ERROR_MSG);
        storage.remove(key);
    }

    @Override
    public void remove(TKey beginKey, TKey endKey) {
        ValidationUtils.checkOnNull(beginKey, "Begin key " + ValidationUtils.NULL_ERROR_MSG);
        ValidationUtils.checkOnNull(endKey, "End key " + ValidationUtils.NULL_ERROR_MSG);

        List<ControlGroup<TKey>> values = new ArrayList<>(storage.values());
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

    protected AbstractMap<TKey, ControlGroup<TKey>> getStorage() { return storage; }

}
