package info.smart_tools.shewhart_charts.modules.verification;

import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class GeneralVerificationModule implements VerificationChartModule {
    private List<VerificationChartModule> verificationChartModules;

    private GeneralVerificationModule(List<VerificationChartModule> verificationChartModules) {
        this.verificationChartModules = verificationChartModules;
    }

    public static GeneralVerificationModule create(@Nonnull List<VerificationChartModule> verificationChartModules) {
        ValidationUtils.checkOnNullOrEmpty(verificationChartModules, "List of the validation chart is null or empty!");
        return new GeneralVerificationModule(verificationChartModules);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> errors) {
        for (VerificationChartModule verificationChartModule : verificationChartModules) {
            verificationChartModule.verify(chartSnapshot, errors);
        }

        return errors.isEmpty();
    }

}
