package org.maminsibirac.shewhart.modules.verification.criteria;

import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReasonFactory;

import java.util.ArrayList;
import java.util.List;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

public class FiveCriteriaValidator implements VerificationChartModule {
    private static final String CRITERIA_NAME = "FIVE_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;

    private FiveCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 2;
        this.reasonFactory = reasonFactory;
    }



    public static FiveCriteriaValidator create(SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FiveCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> reasons) {
        double centralLine = chartSnapshot.getCentralLine();
        double upperCentralLine = chartSnapshot.getUpperCentralLine();
        double lowerCentralLine = chartSnapshot.getLowerCentralLine();

        double partArea = (centralLine - lowerCentralLine) / 6;
        double topAreaABound = upperCentralLine - partArea;
        double bottomAreaABound = lowerCentralLine + partArea;

        List<Measurement<TKey, Double>> measurements = chartSnapshot.getChartValues();
        List<Measurement<TKey, Double>> errorMeasuremets = new ArrayList<>(measurements.size());
        int j = 0;
        for (Measurement<TKey, Double> measurement : measurements) {

        }

        return false;
    }
}
