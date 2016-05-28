package info.smart_tools.shewhart_charts.modules.verification.criteria;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.utils.ComparatorHelper;

import javax.annotation.Nonnull;
import java.util.*;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

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

    public static SecondCriteriaValidator create(int bound, @Nonnull SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SecondCriteriaValidator(bound, reasonFactory);
    }

    public static SecondCriteriaValidator create(@Nonnull SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SecondCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        if (values.size() < bound)
            return true;

        double centralLine = chartSnapshot.getCentralLine();
        boolean verifyResult = verify(values, centralLine, ComparatorHelper.getStandartComparator(), reasons);
        verifyResult &= verify(values, centralLine, ComparatorHelper.getInverseComparator(), reasons);

        return verifyResult;
    }

    private <TKey extends Comparable<TKey>>
    boolean verify(
            List<Measurement<TKey, Double>> measurements,
            double centralLine,
            Comparator<Double> condition,
            List<SpecialReason<TKey>> reasons
    ) {
        Map<Integer, Measurement<TKey, Double>> errorMeasurements = new HashMap<>();
        int j = 0;
        boolean isAccept = true;
        for (Measurement<TKey, Double> measurement : measurements) {
            if (condition.compare(measurement.getValue(), centralLine) == 1) {
                if (j >= bound) {
                    isAccept = false;
                    reasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
                    errorMeasurements = new HashMap<>();
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
