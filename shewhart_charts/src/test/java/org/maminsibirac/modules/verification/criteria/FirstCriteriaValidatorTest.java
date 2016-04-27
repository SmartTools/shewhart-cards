package org.maminsibirac.modules.verification.criteria;

import org.junit.Before;
import org.junit.Test;
import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.charts.snapshots.GeneralChartSnapshotBuilder;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.criteria.FirstCriteriaValidator;
import org.maminsibirac.shewhart.modules.verification.reasons.GeneralReasonFactory;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class FirstCriteriaValidatorTest {
    private VerificationChartModule firstCriteriaValidator;

    @Before
    public void setUp() {
        firstCriteriaValidator = FirstCriteriaValidator.create(GeneralReasonFactory.create());
    }

    @Test
    public void verify_Success_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.2033));
        values.add(Measurement.create(2, 0.2045));
        values.add(Measurement.create(3, 0.2100));
        values.add(Measurement.create(4, 0.1965));
        values.add(Measurement.create(5, 0.1854));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertTrue(firstCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void verify_Fail_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.2134));
        values.add(Measurement.create(2, 0.2045));
        values.add(Measurement.create(3, 0.2100));
        values.add(Measurement.create(4, 0.1623));
        values.add(Measurement.create(5, 0.1687));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(firstCriteriaValidator.verify(chartSnapshot, errors));
        assertEquals(errors.size(), 3);
    }
}
