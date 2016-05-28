package info.smart_tools.shewhart_charts.utils;

import java.util.HashMap;
import java.util.Map;

public class Coefficients {

    private Map<Integer, Map<String, Double>> values;

    public Coefficients() {
        this.values = new HashMap<>();
    }

    public Coefficients(final Map<Integer, Map<String, Double>> values) {
        this.values = values;
    }

    public Coefficients add(final int dimension, final String key, Double value) {
        if (!values.containsKey(dimension)) {
            Map<String, Double> param = new HashMap<>();
            param.put(key, value);
            values.put(dimension, param);
        } else {
            values.get(dimension).put(key, value);
        }

        return this;
    }

    public Double get(final int combinationCount, final String key) {
        return values.get(combinationCount).get(key);
    }
}
