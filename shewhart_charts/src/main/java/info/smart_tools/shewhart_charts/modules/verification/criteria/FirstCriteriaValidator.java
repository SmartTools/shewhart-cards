package info.smart_tools.shewhart_charts.modules.verification.criteria;


import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReasonFactory;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class FirstCriteriaValidator implements VerificationModule {
    private static final String CRITERIA_NAME = "FIRST_CRITERIA";
    private SpecialReasonFactory reasonFactory;

    private FirstCriteriaValidator(SpecialReasonFactory reasonFactory) {
        this.reasonFactory = reasonFactory;
    }

    public static FirstCriteriaValidator create(@Nonnull SpecialReasonFactory reasonFactory) {
        ValidationUtils.checkOnNull(reasonFactory, "Reason factory is null!");
        return new FirstCriteriaValidator(reasonFactory);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> reasons) {
        double upperCentralLine = chartSnapshot.getUpperCentralLine();
        double lowerCentralLine = chartSnapshot.getLowerCentralLine();
        List<Measurement<TKey, Double>> values = chartSnapshot.getChartValues();
        boolean isAccept = true;
        for (Measurement<TKey, Double> measurement : values) {
            double value = measurement.getValue();
            if (value > upperCentralLine || value < lowerCentralLine) {
                isAccept = false;
                reasons.add(reasonFactory.create(CRITERIA_NAME, Collections.singletonList(measurement)));
            }
        }

        return isAccept;
    }
}
