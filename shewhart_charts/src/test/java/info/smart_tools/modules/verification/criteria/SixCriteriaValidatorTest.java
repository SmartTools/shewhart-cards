package info.smart_tools.modules.verification.criteria;

import org.junit.Before;
import org.junit.Test;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.criteria.SixCriteriaValidator;
import info.smart_tools.shewhart_charts.modules.verification.reasons.GeneralReasonFactory;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SixCriteriaValidatorTest {
    private VerificationModule sixCriteriaValidator;

    @Before
    public void setUp() {
        sixCriteriaValidator = SixCriteriaValidator.create(GeneralReasonFactory.create());
    }

    /**
     * Top bound for area <areaName>B</areaName> := 0,199366666
     * Bottom bound for area <areaName>B</areaName> := 0,185433333
     */
    @Test
    public void should_VerifySuccessTest() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.2092));
        values.add(Measurement.create(2, 0.1996));
        values.add(Measurement.create(3, 0.1997));
        values.add(Measurement.create(4, 0.1845));
        values.add(Measurement.create(5, 0.1793));
        values.add(Measurement.create(6, 0.1056));
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

        assertTrue(sixCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.isEmpty());
    }

    /**
     * Top bound for area <areaName>B</areaName> := 0,199366666
     * Bottom bound for area <areaName>B</areaName> := 0,185433333
     */
    @Test
    public void should_VerifyFail_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.1092));
        values.add(Measurement.create(2, 0.2926));
        values.add(Measurement.create(3, 0.2221));
        values.add(Measurement.create(4, 0.2865));
        values.add(Measurement.create(5, 0.2733));
        values.add(Measurement.create(6, 0.2056));
        values.add(Measurement.create(7, 0.1720));
        values.add(Measurement.create(8, 0.1646));
        values.add(Measurement.create(9, 0.1232));
        values.add(Measurement.create(10, 0.1132));
        values.add(Measurement.create(11, 0.1232));
        values.add(Measurement.create(12, 0.2062));
        values.add(Measurement.create(13, 0.1932));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(sixCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.size() == 6);
    }
}
