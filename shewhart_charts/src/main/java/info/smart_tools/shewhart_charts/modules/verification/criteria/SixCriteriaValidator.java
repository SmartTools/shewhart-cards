package info.smart_tools.shewhart_charts.modules.verification.criteria;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.utils.ComparatorHelper;
import info.smart_tools.shewhart_charts.utils.ListUtils;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.*;

public class SixCriteriaValidator implements VerificationChartModule {
    private static final String CRITERIA_NAME = "SIX_CRITERIA";
    private SpecialReasonFactory reasonFactory;
    private int bound;
    private int errorBound;

    private SixCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.bound = 5;
        this.errorBound = 4;
        this.reasonFactory = reasonFactory;
    }

    public static SixCriteriaValidator create(SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SixCriteriaValidator(reasonFactory);
    }

    private SixCriteriaValidator(int bound, int errorBound, @Nonnull SpecialReasonFactory reasonFactory) {
        this.bound = bound;
        this.errorBound = errorBound;
        this.reasonFactory = reasonFactory;
    }

    public static SixCriteriaValidator create(int bound, int errorBound, @Nonnull SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory should not be a null!");
        return new SixCriteriaValidator(bound, errorBound, reasonFactory);
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
        double topAreaBBound = upperCentralLine - 2 * partArea;
        double bottomAreaBBound = lowerCentralLine + 2 * partArea;

        boolean verifyResult = verify(values, topAreaBBound, ComparatorHelper.getStandartComparator(), reasons);
        verifyResult &= verify(values, bottomAreaBBound, ComparatorHelper.getInverseComparator(), reasons);

        return verifyResult;
    }

    private <TKey extends Comparable<TKey>>
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
            ListUtils.copy(block, measurements, i, blockLength);
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
