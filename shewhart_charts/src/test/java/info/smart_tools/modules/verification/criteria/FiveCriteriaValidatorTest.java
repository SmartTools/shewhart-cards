package info.smart_tools.modules.verification.criteria;

import info.smart_tools.shewhart_charts.modules.verification.VerificationException;
import org.junit.Before;
import org.junit.Test;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.criteria.FiveCriteriaValidator;
import info.smart_tools.shewhart_charts.modules.verification.reasons.GeneralReasonFactory;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FiveCriteriaValidatorTest {
    private VerificationModule fiveCriteriaValidator;

    @Before
    public void setUp() {
        fiveCriteriaValidator = FiveCriteriaValidator.create(GeneralReasonFactory.create());
    }

    /**
     * Top bound for area <areaName>A</areaName> := 0.2063
     * Bottom bound for area <areaName>A</areaName> := 0.1785
     */
    @Test
    public void should_VerifySuccessTest() throws VerificationException {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.2092));
        values.add(Measurement.create(2, 0.1926));
        values.add(Measurement.create(3, 0.1721));
        values.add(Measurement.create(4, 0.1865));
        values.add(Measurement.create(5, 0.1793));
        values.add(Measurement.create(6, 0.2056));
        values.add(Measurement.create(7, 0.1920));
        values.add(Measurement.create(8, 0.1946));
        values.add(Measurement.create(9, 0.1932));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertTrue(fiveCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.isEmpty());
    }

    /**
     * Top bound for area <areaName>A</areaName> := 0.2063
     * Bottom bound for area <areaName>A</areaName> := 0.1785
     */
    @Test
    public void should_VerifyFail_Test() throws VerificationException {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.2092));
        values.add(Measurement.create(2, 0.1926));
        values.add(Measurement.create(3, 0.2221));
        values.add(Measurement.create(4, 0.2865));
        values.add(Measurement.create(5, 0.1733));
        values.add(Measurement.create(6, 0.2056));
        values.add(Measurement.create(7, 0.1720));
        values.add(Measurement.create(8, 0.1646));
        values.add(Measurement.create(9, 0.1932));
        values.add(Measurement.create(10, 0.2132));
        values.add(Measurement.create(11, 0.2232));
        values.add(Measurement.create(12, 0.2062));
        values.add(Measurement.create(13, 0.1932));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(fiveCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.size() == 8);
    }

}
