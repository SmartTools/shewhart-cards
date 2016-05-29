package info.smart_tools.modules.verification.criteria;

import org.junit.Before;
import org.junit.Test;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.modules.verification.VerificationModule;
import info.smart_tools.shewhart_charts.modules.verification.criteria.SevenCriteriaValidator;
import info.smart_tools.shewhart_charts.modules.verification.reasons.GeneralReasonFactory;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SevenCriteriaValidatorTest {
    private VerificationModule sevenCriteriaValidator;

    @Before
    public void setUp() {
        sevenCriteriaValidator = SevenCriteriaValidator.create(5, GeneralReasonFactory.create());
    }

    /**
     * Top bound for area <areaName>B</areaName> := 0,199366666
     * Bottom bound for area <areaName>B</areaName> := 0,185433333
     */
    @Test
    public void should_VerifySuccess_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.1926));
        values.add(Measurement.create(2, 0.1928));
        values.add(Measurement.create(3, 0.1922));
        values.add(Measurement.create(4, 0.1924));
        values.add(Measurement.create(5, 0.2054));
        values.add(Measurement.create(6, 0.1906));
        values.add(Measurement.create(7, 0.1920));
        values.add(Measurement.create(8, 0.1946));
        values.add(Measurement.create(9, 0.1932));
        values.add(Measurement.create(10, 0.2120));
        values.add(Measurement.create(11, 0.1946));
        values.add(Measurement.create(12, 0.1932));

        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertTrue(sevenCriteriaValidator.verify(chartSnapshot, errors));
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
        values.add(Measurement.create(1, 0.2126));
        values.add(Measurement.create(2, 0.1926));
        values.add(Measurement.create(3, 0.1922));
        values.add(Measurement.create(4, 0.1965));
        values.add(Measurement.create(5, 0.1924));
        values.add(Measurement.create(6, 0.1976));
        values.add(Measurement.create(7, 0.1920));
        values.add(Measurement.create(8, 0.2020));
        values.add(Measurement.create(9, 0.1932));
        values.add(Measurement.create(10, 0.1906));
        values.add(Measurement.create(11, 0.1950));
        values.add(Measurement.create(12, 0.1946));
        values.add(Measurement.create(13, 0.1986));
        values.add(Measurement.create(14, 0.2346));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(sevenCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.size() == 2);
        assertTrue(errors.get(0).getPointKeys().size() == 6
                        && errors.get(0).getPointKeys().get(0).equals(Measurement.create(2, 0.1926))
                        && errors.get(0).getPointKeys().get(5).equals(Measurement.create(7, 0.1920))
        );
        assertTrue(errors.get(1).getPointKeys().size() == 5
                        && errors.get(1).getPointKeys().get(0).equals(Measurement.create(9, 0.1932))
                        && errors.get(1).getPointKeys().get(4).equals(Measurement.create(13, 0.1986))
        );
    }

}
