package org.maminsibirac.shewhart.groups.quantitative;

import org.maminsibirac.shewhart.utils.ValidationUtils;

public class ISRChartsGroup<TValue extends Number, TKey extends Comparable<TKey>>
        implements ISRChartsControlGroup<TValue, TKey> {
    private TKey key;
    private TValue value;

    private ISRChartsGroup(TKey key) {
        this.key = key;
    }

    private ISRChartsGroup(TKey key, TValue value) {
        this.key = key;
        this.value = value;
    }

    public static <TValue extends Number, TKey extends Comparable<TKey>>
    ISRChartsControlGroup<TValue, TKey> create(TKey key) {
        ValidationUtils.checkOnNull(key, "");
        return new ISRChartsGroup<>(key);
    }

    public static <TValue extends Number, TKey extends Comparable<TKey>>
    ISRChartsControlGroup<TValue, TKey> create(TKey key, TValue value) {
        ValidationUtils.checkOnNull(key, "");
        ValidationUtils.checkOnNull(value, "");
        return new ISRChartsGroup<>(key, value);
    }

    @Override
    public void add(TValue value) {
        ValidationUtils.checkOnNull(value, "");
        this.value = value;
    }

    @Override
    public TValue get() {
        return value;
    }

    @Override
    public TKey getKey() {
        return key;
    }
}
