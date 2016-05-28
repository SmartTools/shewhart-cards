package info.smart_tools.shewhart_charts.modules.verification.reasons;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GeneralReason<TKey extends Comparable<TKey>> implements SpecialReason<TKey> {
    private String name;
    private String message;
    private List<Measurement<TKey, Double>> errorValues;

    private GeneralReason(String name, List<Measurement<TKey, Double>> errorValues) {
        this.name = name;
        this.errorValues = new ArrayList<>(errorValues);
        this.message = createMessageOf(name, errorValues);
    }

    public static <TKey extends Comparable<TKey>> GeneralReason<TKey>
    create(@Nonnull String name, @Nonnull List<Measurement<TKey, Double>> keys) {
        ValidationUtils.checkOnNullOrEmpty(name, "");
        ValidationUtils.checkOnNullOrEmpty(keys, "");

        return new GeneralReason<>(name, keys);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<Measurement<TKey, Double>> getPointKeys() {
        return errorValues;
    }

    private String createMessageOf(String name, List<Measurement<TKey, Double>> errorValues) {
        StringBuilder message = new StringBuilder();
        message
                .append("Shewhart criteria with name - ")
                .append(name)
                .append(" was caused by the points: ");
        for (Measurement<TKey, Double> value : errorValues) {
            message
                    .append("key - ")
                    .append(value.getKey())
                    .append(" ,")
                    .append("value - ")
                    .append(value.getValue())
                    .append(";");
        }

        return message.toString();
    }
}
