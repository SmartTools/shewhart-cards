package org.maminsibirac.modules.verification.criteria;

import org.junit.Before;
import org.junit.Test;
import org.maminsibirac.shewhart.charts.Measurement;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshot;
import org.maminsibirac.shewhart.charts.snapshots.ChartSnapshotBuilder;
import org.maminsibirac.shewhart.charts.snapshots.GeneralChartSnapshotBuilder;
import org.maminsibirac.shewhart.modules.verification.VerificationChartModule;
import org.maminsibirac.shewhart.modules.verification.criteria.SecondCriteriaValidator;
import org.maminsibirac.shewhart.modules.verification.reasons.GeneralReasonFactory;
import org.maminsibirac.shewhart.modules.verification.reasons.SpecialReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class SecondCriteriaValidationTest {
    private VerificationChartModule secondCriteriaValidator;

    @Before
    public void setUp() {
        secondCriteriaValidator = SecondCriteriaValidator.create(3, GeneralReasonFactory.create());
    }

    @Test
    public void verify_Success_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.19241));
        values.add(Measurement.create(2, 0.1926));
        values.add(Measurement.create(3, 0.1922));
        values.add(Measurement.create(4, 0.1865));
        values.add(Measurement.create(5, 0.1954));
        values.add(Measurement.create(6, 0.1906));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertTrue(secondCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void verify_Fail_WhenBelowAndAboveCentralLine_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.19241));
        values.add(Measurement.create(2, 0.1926));
        values.add(Measurement.create(3, 0.1932));
        values.add(Measurement.create(4, 0.1945));
        values.add(Measurement.create(5, 0.1922));
        values.add(Measurement.create(6, 0.1965));
        values.add(Measurement.create(7, 0.1902));
        values.add(Measurement.create(8, 0.1865));
        values.add(Measurement.create(9, 0.1854));
        values.add(Measurement.create(10, 0.1913));
        values.add(Measurement.create(11, 0.1942));
        values.add(Measurement.create(12, 0.1923));
        values.add(Measurement.create(13, 0.1965));
        values.add(Measurement.create(14, 0.1926));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(secondCriteriaValidator.verify(chartSnapshot, errors));
        assertEquals(errors.size(), 2);
        assertEquals(errors.get(0).getPointKeys().size(), 4);
        assertEquals(errors.get(1).getPointKeys().size(), 4);
    }

}
