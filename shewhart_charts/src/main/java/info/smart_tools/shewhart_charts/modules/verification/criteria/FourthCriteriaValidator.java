package info.smart_tools.shewhart_charts.modules.verification.criteria;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FourthCriteriaValidator implements VerificationModule {
    private static final String CRITERIA_NAME = "FOURTH_CRITERIA";
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

    public static FourthCriteriaValidator create(int bound, @Nonnull SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FourthCriteriaValidator(bound, reasonFactory);
    }

    public static FourthCriteriaValidator create(@Nonnull SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FourthCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> measurements = chartSnapshot.getChartValues();
        if (measurements.size() < bound)
            return true;

        Map<Integer, Measurement<TKey, Double>> errorMeasurements = new HashMap<>();
        int[] amplitude = getAmplitude(measurements);
        int j = 0;
        boolean isAccept = true;
        for (int i = 0; i < amplitude.length; ++i) {
            if (amplitude[i] == 0 || (amplitude[i] + amplitude[i - 1] != 0)
                    && (amplitude[i - 1] != 0) && (amplitude[i] != 0)) {
                if (j >= bound) {
                    isAccept = false;
                    reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
                    errorMeasurements = new HashMap<>();
                }
                j = 0;
                errorMeasurements.put(j++, measurements.get(i));
                continue;
            }
            errorMeasurements.put(j++, measurements.get(i));
        }
        if (j >= bound) {
            isAccept = false;
            reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
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
