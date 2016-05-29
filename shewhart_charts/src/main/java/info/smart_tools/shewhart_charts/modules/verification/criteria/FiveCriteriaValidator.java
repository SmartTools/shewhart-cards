package info.smart_tools.shewhart_charts.modules.verification.criteria;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import javax.annotation.Nonnull;
import java.util.*;

import static info.smart_tools.shewhart_charts.utils.ComparatorHelper.getInverseComparator;
import static info.smart_tools.shewhart_charts.utils.ComparatorHelper.getStandartComparator;
import static info.smart_tools.shewhart_charts.utils.ListUtils.copy;
import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNull;

public class FiveCriteriaValidator implements VerificationModule {
    private static final String CRITERIA_NAME = "FIVE_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;
    private int errorBound;

    private FiveCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 3;
        this.errorBound = 2;
        this.reasonFactory = reasonFactory;
    }

    public static FiveCriteriaValidator create(@Nonnull SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FiveCriteriaValidator(reasonFactory);
    }

    private FiveCriteriaValidator(int bound, int errorBound, SpecialReasonFactory reasonFactory) {
        this.bound = bound;
        this.errorBound = errorBound;
        this.reasonFactory = reasonFactory;
    }

    public static FiveCriteriaValidator create(int bound, int errorBound, SpecialReasonFactory reasonFactory) {
        checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new FiveCriteriaValidator(bound, errorBound, reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> reasons) {
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        if (values.size() < bound)
            return true;

        double upperCentralLine = chartSnapshot.getUpperCentralLine();
        double lowerCentralLine = chartSnapshot.getLowerCentralLine();

        double partArea = (upperCentralLine - lowerCentralLine) / 6;
        double topAreaABound = upperCentralLine - partArea;
        double bottomAreaABound = lowerCentralLine + partArea;

        boolean verifyResult = verify(values, topAreaABound, getStandartComparator(), reasons);
        verifyResult &= verify(values, bottomAreaABound, getInverseComparator(), reasons);

        return verifyResult;
    }

    private  <TKey extends Comparable<TKey>>
    boolean verify(
            List<Measurement<TKey, Double>> measurements,
            double controlLine,
            Comparator<Double> condition,
            List<SpecialReason<TKey>> reasons
    ) {
        boolean isAccept = true;
        int measSize = measurements.size();
        List<Measurement<TKey, Double>> block = new ArrayList<>(bound);
        int blockLength;
        for (int i = 0; i < measSize; ++i) {
            blockLength = measSize - i > bound ? bound : measSize - i;
            copy(block, measurements, i, blockLength);
            if (!verifyBlock(block, condition, controlLine)) {
                reasons.add(reasonFactory.create(CRITERIA_NAME, block));
                isAccept = false;
            }
        }

        return isAccept;
    }

    private <TKey extends Comparable<TKey>>
    boolean verifyBlock(
            List<Measurement<TKey, Double>> block,
            Comparator<Double> condition,
            double controlLine
    ) {
        int errorCount = 0;
        for (Measurement<? extends Comparable<?>, Double> value : block)
            if (condition.compare(value.getValue(), controlLine) == 1)
                ++errorCount;

        return errorCount < errorBound;
    }
}
