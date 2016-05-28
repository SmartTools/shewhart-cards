package info.smart_tools.shewhart_charts.modules.verification.criteria;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.utils.ComparatorHelper;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.*;

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

    public static ThirdCriteriaValidator create(int bound, @Nonnull SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new ThirdCriteriaValidator(bound, reasonFactory);
    }

    public static ThirdCriteriaValidator create(@Nonnull SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new ThirdCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        if (values.size() < bound)
            return true;

        boolean verifyResult = verify(values, 0.0, ComparatorHelper.getStandartComparator(), reasons);
        verifyResult &= verify(values, Double.MAX_VALUE, ComparatorHelper.getInverseComparator(), reasons);

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
                    errorMeasurements = new HashMap<>();
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
