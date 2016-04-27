package org.maminsibirac.shewhart.groups.quantitative;

import java.util.*;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;
import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNullOrEmpty;

// ToDo : write error messages.
public class ARChartsGroup<TData extends Number, TKey extends Comparable<TKey>>
        implements ARChartsControlGroup<TData, TKey> {
    private TKey key;
    private AbstractMap<String, TData> data;

    private ARChartsGroup(TKey key) {
        this.key = key;
        this.data = new TreeMap<>();
    }

    private ARChartsGroup(TKey key, Map<String, TData> data) {
        this.key = key;
        this.data = new TreeMap<>(data);
    }

    public static <TData extends Number, TKey extends Comparable<TKey>>
    ARChartsControlGroup<TData, TKey> create(TKey key) {
        checkOnNull(key, "");
        return new ARChartsGroup<>(key);
    }

    public static <TData extends Number, TKey extends Comparable<TKey>>
    ARChartsControlGroup<TData, TKey> create(TKey key, Map<String, TData> data) {
        checkOnNull(key, "");
        checkOnNullOrEmpty(data, "");
        return new ARChartsGroup<>(key, data);
    }

    @Override
    public void add(String key, TData value) {
        checkOnNullOrEmpty(key, "");
        checkOnNull(value, "");
        data.put(key, value);
    }

    @Override
    public TData get(String key) {
        checkOnNullOrEmpty(key, "");
        return data.get(key);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public List<TData> values() {
        return new ArrayList<>(data.values());
    }

    @Override
    public TKey getKey() {
        return key;
    }

}
