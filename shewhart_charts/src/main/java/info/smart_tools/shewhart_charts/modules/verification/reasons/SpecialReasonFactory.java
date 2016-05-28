package info.smart_tools.shewhart_charts.modules.verification.reasons;

import info.smart_tools.shewhart_charts.utils.Measurement;

import javax.annotation.Nonnull;
import java.util.List;

public interface SpecialReasonFactory {
    <TKey extends Comparable<TKey>> SpecialReason<TKey>
    create(@Nonnull String name, @Nonnull List<Measurement<TKey, Double>> keys);
}
