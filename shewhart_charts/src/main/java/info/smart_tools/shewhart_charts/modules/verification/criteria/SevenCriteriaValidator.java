package info.smart_tools.shewhart_charts.modules.verification.criteria;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

public class SevenCriteriaValidator implements VerificationModule {
    private static final String CRITERIA_NAME = "SEVEN_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;

    private SevenCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 15;
        this.reasonFactory = reasonFactory;
    }

    public static SevenCriteriaValidator create(@Nonnull SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SevenCriteriaValidator(reasonFactory);
    }

    private SevenCriteriaValidator(int bound, @Nonnull SpecialReasonFactory reasonFactory) {
        this.bound = bound;
        this.reasonFactory = reasonFactory;
    }

    public static SevenCriteriaValidator create(int bound, @Nonnull SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SevenCriteriaValidator(bound, reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> specialReasons) {
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        if (values.size() < bound)
            return true;

        double centralLine = chartSnapshot.getCentralLine();
        double upperCentralLine = chartSnapshot.getUpperCentralLine();
        double lowerCentralLine = chartSnapshot.getLowerCentralLine();

        double partArea = (upperCentralLine - lowerCentralLine) / 6;
        double topAreaCBound = centralLine + partArea;
        double bottomAreaCBound = centralLine - partArea;

        int j = 0;
        boolean verifyResult = true;
        Map<Integer, Measurement<TKey, Double>> errorMeasurements = new HashMap<>();
        for (Measurement<TKey, Double> measurement : values) {
            if (measurement.getValue() <= bottomAreaCBound || measurement.getValue() >= topAreaCBound) {
                if (j >= bound) {
                    if (isAccept(errorMeasurements, centralLine)) {
                        verifyResult = false;
                        specialReasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
                    }
                    errorMeasurements = new HashMap<>();
                }
                j = 0;
                continue;
            }
            errorMeasurements.put(j++, measurement);
        }
        if (j >= bound && isAccept(errorMeasurements, centralLine)) {
            verifyResult = false;
            specialReasons.add(reasonFactory.create(CRITERIA_NAME, new ArrayList<>(errorMeasurements.values())));
        }

        return verifyResult;
    }

    private <TKey extends Comparable<TKey>>
    boolean isAccept(Map<Integer, Measurement<TKey, Double>> values, double controlLine) {
        int lessCount = 0, moreCount = 0;
        for (Map.Entry<Integer, Measurement<TKey, Double>> entryValue : values.entrySet()) {
            if (entryValue.getValue().getValue() > controlLine)
                ++moreCount;
            else
                ++lessCount;
        }

        return lessCount != 0 && moreCount != 0;
    }

}
