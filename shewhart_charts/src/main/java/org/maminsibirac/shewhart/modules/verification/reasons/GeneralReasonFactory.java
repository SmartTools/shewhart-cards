package org.maminsibirac.shewhart.modules.verification.reasons;

import org.maminsibirac.shewhart.charts.Measurement;

import java.util.List;

public class GeneralReasonFactory implements SpecialReasonFactory {
    private GeneralReasonFactory() {}

    public static GeneralReasonFactory create() {
        return new GeneralReasonFactory();
    }

    @Override
    public <TKey extends Comparable<TKey>> GeneralReason<TKey>
    create(String name, List<Measurement<TKey, Double>> errorValues) {
        return GeneralReason.create(name, errorValues);
    }
}
