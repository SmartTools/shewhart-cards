package org.maminsibirac.shewhart.modules.verification.criteria;


import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReasonFactory;

import java.util.Collections;
import java.util.List;

public class FirstCriteriaValidator implements VerificationChartModule {
    private static final String CRITERIA_NAME = "FIRST_CRITERIA";
    private SpecialReasonFactory reasonFactory;

    private FirstCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.reasonFactory = reasonFactory;
    }

    public static FirstCriteriaValidator create(SpecialReasonFactory reasonFactory) {
        if (reasonFactory == null)
            throw new IllegalArgumentException("Reason factory is null!");
        return new FirstCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons) {
        double upperCentralLine = chartSnapshot.getUpperCentralLine();
        double lowerCentralLine = chartSnapshot.getLowerCentralLine();
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        boolean isAccept = true;
        for (Measurement<TKey, Double> measurement : values) {
            double value = measurement.getValue();
            if (value > upperCentralLine || value < lowerCentralLine) {
                isAccept = false;
                reasons.add(reasonFactory.create(CRITERIA_NAME, Collections.singletonList(measurement)));
            }
        }

        return isAccept;
    }
}
