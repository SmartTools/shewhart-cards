package info.smart_tools.shewhart_charts.modules.verification;

import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class GeneralVerificationModule implements VerificationModule {
    private List<VerificationModule> verificationModules;

    private GeneralVerificationModule(List<VerificationModule> verificationModules) {
        this.verificationModules = verificationModules;
    }

    public static GeneralVerificationModule create(@Nonnull List<VerificationModule> verificationModules) {
        ValidationUtils.checkOnNullOrEmpty(verificationModules, "List of the validation chart is null or empty!");
        return new GeneralVerificationModule(verificationModules);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(@Nonnull ChartSnapshot<TKey> chartSnapshot, @Nonnull List<SpecialReason<TKey>> errors) {
        for (VerificationModule verificationModule : verificationModules) {
            verificationModule.verify(chartSnapshot, errors);
        }

        return errors.isEmpty();
    }

}
