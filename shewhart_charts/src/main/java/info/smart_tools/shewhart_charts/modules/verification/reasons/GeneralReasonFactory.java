package info.smart_tools.shewhart_charts.modules.verification.reasons;

import info.smart_tools.shewhart_charts.utils.Measurement;

import javax.annotation.Nonnull;
import java.util.List;

public class GeneralReasonFactory implements SpecialReasonFactory {
    private GeneralReasonFactory() {}

    public static GeneralReasonFactory create() {
        return new GeneralReasonFactory();
    }

    @Override
    public <TKey extends Comparable<TKey>> GeneralReason<TKey>
    create(@Nonnull String name, @Nonnull List<Measurement<TKey, Double>> errorValues) {
        return GeneralReason.create(name, errorValues);
    }
}
