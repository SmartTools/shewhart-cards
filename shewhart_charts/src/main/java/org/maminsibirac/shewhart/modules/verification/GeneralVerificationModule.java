package org.maminsibirac.shewhart.modules.verification;

import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;

import java.util.List;

public class GeneralVerificationModule implements VerificationChartModule {
    private List<VerificationChartModule> verificationChartModules;

    private GeneralVerificationModule(List<VerificationChartModule> verificationChartModules) {
        this.verificationChartModules = verificationChartModules;
    }

    public static GeneralVerificationModule create(List<VerificationChartModule> verificationChartModules) {
        if (verificationChartModules == null || verificationChartModules.isEmpty())
            throw new IllegalArgumentException("List of the validation chart com.clay.maminsibirac.modules is null or empty!");
        return new GeneralVerificationModule(verificationChartModules);
    }

    @Override
    public <TKey extends Comparable<TKey>>
    boolean verify(ChartSnapshot<TKey> chartSnapshot, List<SpecialReason<TKey>> errors) {
        for (VerificationChartModule verificationChartModule : verificationChartModules) {
            verificationChartModule.verify(chartSnapshot, errors);
        }

        return errors.isEmpty();
    }

}
