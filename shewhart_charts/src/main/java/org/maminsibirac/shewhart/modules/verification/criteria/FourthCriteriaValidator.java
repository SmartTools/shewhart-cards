package org.maminsibirac.shewhart.modules.verification.criteria;

import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReasonFactory;
import org.maminsibirac.shewhart.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class FourthCriteriaValidator implements VerificationChartModule {
    private static final String CRITERIA_NAME = "SECOND_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;

    private FourthCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 14;
        this.reasonFactory = reasonFactory;
    }

    private FourthCriteriaValidator(int bound, SpecialReasonFactory reasonFactory) {
        this.bound = bound;
        this.reasonFactory = reasonFactory;
    }

    public static FourthCriteriaValidator create(int bound, SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FourthCriteriaValidator(bound, reasonFactory);
    }

    public static FourthCriteriaValidator create(SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FourthCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> measurements = chartSnapshot.getChartValues();
        if (measurements.size() < bound)
            return true;

        List<Measurement<TKey, Double>> errorMeasurements = new ArrayList<>(measurements.size());
        int[] amplitude = getAmplitude(measurements);
        int j = 0;
        boolean isAccept = true;
        for (int i = 0; i < amplitude.length; ++i) {
            if (amplitude[i + 1] == 0)
                continue;
            errorMeasurements.add(j++, measurements.get(i));
            i += 2;
            for (;i < amplitude.length; ++i) {
                if (amplitude[i] + amplitude[i - 1] != 0) {
                    j = 0;
                    break;
                }

                errorMeasurements.add(j++, measurements.get(i));

                if (j == bound) {
                    j = 0;
                    isAccept = false;
                    reasons.add(reasonFactory.create(CRITERIA_NAME, errorMeasurements));
                }
            }
        }

        return isAccept;
    }

    private <TKey extends Comparable<TKey>>
    int[] getAmplitude(List<Measurement<TKey, Double>> values) {
        int[] amplitude = new int[values.size()];
        amplitude[0] = 0;
        for (int i = 1; i < values.size(); ++i) {
            amplitude[i] = Double.compare(
                    values.get(i).getValue(),
                    values.get(i - 1).getValue()
            );
        }

        return amplitude;
    }

}
