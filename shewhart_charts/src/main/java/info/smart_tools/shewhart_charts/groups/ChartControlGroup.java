package info.smart_tools.shewhart_charts.groups;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface ChartControlGroup<TKey extends Comparable<TKey>, TValue extends Number>
        extends Comparable<ChartControlGroup<TKey, TValue>> {

    TKey getKey();
    TValue get(@Nonnull String key);
    void add(@Nonnull String key, @Nonnull TValue value);
    int size();
    Map<String, TValue> getAll();
    List<TValue> values();
}
