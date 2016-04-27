package org.maminsibirac.shewhart.modules.verification.criteria;

import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReasonFactory;

import java.util.*;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

public class ThirdCriteriaValidator implements VerificationChartModule {
    private static final String CRITERIA_NAME = "THIRD_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;

    private ThirdCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 6;
        this.reasonFactory = reasonFactory;
    }

    private ThirdCriteriaValidator(int bound, SpecialReasonFactory reasonFactory) {
        this.bound = bound;
        this.reasonFactory = reasonFactory;
    }

    public static ThirdCriteriaValidator create(int bound, SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new ThirdCriteriaValidator(bound, reasonFactory);
    }

    public static ThirdCriteriaValidator create(SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new ThirdCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        if (values.size() < bound)
            return true;

        boolean verifyResult = verify(values, 0.0,
                (Double var1, Double var2) -> {
                    if (var1 > var2)
                        return 1;
                    if (var1 < var2)
                        return -1;
                    else
                        return 0;
                },
                reasons
        );
        verifyResult &= verify(values, Double.MAX_VALUE,
                (Double var1, Double var2) -> {
                    if (var1 > var2)
                        return -1;
                    if (var1 < var2)
                        return 1;
                    else
                        return 0;
                },
                reasons
        );

        return verifyResult;
    }

    private <TKey extends Comparable<TKey>>
    boolean verify(
            List<Measurement<TKey, Double>> measurements,
            double measureValue,
            Comparator<Double> condition,
            List<SpecialReason<TKey>> reasons) {
        int j = 0;
        double measure = measureValue;
        boolean isAccept = true;
        Map<Integer, Measurement<TKey, Double>> errorMeasurements = new HashMap<>(measurements.size());
        for (Measurement<TKey, Double> measurement : measurements) {
            if (condition.compare(measurement.getValue(), measure) != 1) {
                if (j >= bound) {
                    isAccept = false;
                    reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
                }
                j = 0;
            }

            measure = measurement.getValue();
            errorMeasurements.put(j++, measurement);
        }
        if (j >= bound) {
            isAccept = false;
            reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
        }

        return isAccept;
    }

}
