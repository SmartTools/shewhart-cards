package info.smart_tools.shewhart_charts.utils;

public class Measurement<TKey extends Comparable<TKey>, TValue extends Number>
        implements Comparable<Measurement<TKey, TValue>> {

    private TKey key;
    private TValue value;

    private Measurement() {}

    private Measurement(TKey key, TValue value) {
        this.key = key;
        this.value = value;
    }

    public static <TValue extends Number, TKey extends Comparable<TKey>>
    Measurement<TKey, TValue> create() {
        return new Measurement<>();
    }

    public static <TValue extends Number, TKey extends Comparable<TKey>>
    Measurement<TKey, TValue> create(TKey key, TValue value) {
        return new Measurement<>(key, value);
    }

    public void setKey(TKey key) {
        this.key = key;
    }

    public void setValue(TValue value) {
        this.value = value;
    }

    public TKey getKey() {
        return key;
    }

    public TValue getValue() {
        return value;
    }

    public boolean equals(Measurement<TKey, TValue> measurement) {
        return this.key.equals(measurement.getKey()) && this.value.equals(measurement.getValue());
    }

    @Override
    public int compareTo(Measurement<TKey, TValue> measurement) {
        return this.key.compareTo(measurement.getKey());
    }
}
