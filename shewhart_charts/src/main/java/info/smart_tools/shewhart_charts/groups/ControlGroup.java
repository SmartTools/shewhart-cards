package info.smart_tools.shewhart_charts.groups;

import javax.annotation.Nonnull;
import java.util.*;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;
import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNullOrEmpty;

public class ControlGroup<TKey extends Comparable<TKey>, TValue extends Number>
        implements ChartControlGroup<TKey, TValue>{

    private TKey key;
    private Map<String, TValue> values;

    private ControlGroup(TKey key) {
        this.key = key;
        this.values = new HashMap<>();
    }

    private ControlGroup(TKey key, Map<String, TValue> values) {
        this.key = key;
        this.values = new HashMap<>(values);
    }

    public static
    <TKey extends Comparable<TKey>, TValue extends Number> ControlGroup<TKey, TValue>
    create(@Nonnull TKey key, @Nonnull Map<String, TValue> values) {
        return new ControlGroup<>(key, values);
    }

    public static
    <TKey extends Comparable<TKey>, TValue extends Number> ControlGroup<TKey, TValue>
    create(@Nonnull TKey key) {
        return new ControlGroup<>(key);
    }

    @Override
    public TKey getKey() {
        return key;
    }

    @Override
    public TValue get(@Nonnull String key) {
        checkOnNullOrEmpty(key, "Key of value");
        return values.get(key);
    }

    @Override
    public void add(@Nonnull String key, @Nonnull TValue value) {
        checkOnNullOrEmpty(key, "Key of value");
        checkOnNull(value, "Value");
        values.put(key, value);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public Map<String, TValue> getAll() {
        return new HashMap<>(values);
    }

    @Override
    public List<TValue> values() {
        return new ArrayList<>(values.values());
    }
}
