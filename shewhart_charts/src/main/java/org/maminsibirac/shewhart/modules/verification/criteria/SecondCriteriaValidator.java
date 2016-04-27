package org.maminsibirac.shewhart.modules.verification.criteria;

import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReasonFactory;

import java.util.*;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

public class SecondCriteriaValidator implements VerificationChartModule {
    private static final String CRITERIA_NAME = "SECOND_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;

    private SecondCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 9;
        this.reasonFactory = reasonFactory;
    }

    private SecondCriteriaValidator(int bound, SpecialReasonFactory reasonFactory) {
        this.bound = bound;
        this.reasonFactory = reasonFactory;
    }

    public static SecondCriteriaValidator create(int bound, SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SecondCriteriaValidator(bound, reasonFactory);
    }

    public static SecondCriteriaValidator create(SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SecondCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        if (values.size() < bound)
            return true;

        double centralLine = chartSnapshot.getCentralLine();
        boolean verifyResult = verify(values, centralLine,
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
        verifyResult &= verify(values, centralLine,
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
            double centralLine,
            Comparator<Double> condition,
            List<SpecialReason<TKey>> reasons
    ) {
        Map<Integer, Measurement<TKey, Double>> errorMeasurements = new HashMap<>(measurements.size());
        int j = 0;
        boolean isAccept = true;
        for (Measurement<TKey, Double> measurement : measurements) {
            if (condition.compare(measurement.getValue(), centralLine) == 1) {
                if (j >= bound) {
                    isAccept = false;
                    reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
                }
                j = 0;
                continue;
            }
            errorMeasurements.put(j++, measurement);
        }
        if (j >= bound) {
            isAccept = false;
            reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
        }

        return isAccept;
    }

}
