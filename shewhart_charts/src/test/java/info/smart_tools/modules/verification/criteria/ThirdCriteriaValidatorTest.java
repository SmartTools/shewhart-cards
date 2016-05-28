package info.smart_tools.modules.verification.criteria;

import org.junit.Before;
import org.junit.Test;
import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshot;
import info.smart_tools.shewhart_charts.snapshots.ChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.snapshots.GeneralChartSnapshotBuilder;
import info.smart_tools.shewhart_charts.modules.verification.VerificationChartModule;
import info.smart_tools.shewhart_charts.modules.verification.criteria.ThirdCriteriaValidator;
import info.smart_tools.shewhart_charts.modules.verification.reasons.GeneralReasonFactory;
import info.smart_tools.shewhart_charts.modules.verification.reasons.SpecialReason;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ThirdCriteriaValidatorTest {
    private VerificationChartModule thirdCriteriaValidator;

    @Before
    public void setUp() {
        thirdCriteriaValidator = ThirdCriteriaValidator.create(GeneralReasonFactory.create());
    }

    @Test
    public void verify_Success_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.1924));
        values.add(Measurement.create(2, 0.1926));
        values.add(Measurement.create(3, 0.1922));
        values.add(Measurement.create(4, 0.1865));
        values.add(Measurement.create(5, 0.1954));
        values.add(Measurement.create(6, 0.1906));
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

        assertTrue(thirdCriteriaValidator.verify(chartSnapshot, errors));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void verify_Fail_WhenDecrease_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.1945));
        values.add(Measurement.create(2, 0.1922));
        values.add(Measurement.create(3, 0.1916));
        values.add(Measurement.create(4, 0.1902));
        values.add(Measurement.create(5, 0.1865));
        values.add(Measurement.create(6, 0.1854));
        values.add(Measurement.create(7, 0.1913));
        values.add(Measurement.create(8, 0.1923));
        values.add(Measurement.create(9, 0.1922));
        values.add(Measurement.create(10, 0.1921));
        values.add(Measurement.create(11, 0.1920));
        values.add(Measurement.create(12, 0.1919));
        values.add(Measurement.create(13, 0.1918));
        values.add(Measurement.create(14, 0.1917));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(thirdCriteriaValidator.verify(chartSnapshot, errors));
        assertEquals(errors.size(), 2);

        assertEquals(errors.get(0).getPointKeys().size(), 6);
        assertEquals(errors.get(1).getPointKeys().size(), 7);

        assertTrue(errors.get(0).getPointKeys().get(0).equals(Measurement.create(1, 0.1945)));
        assertTrue(errors.get(0).getPointKeys().get(5).equals(Measurement.create(6, 0.1854)));
        assertTrue(errors.get(1).getPointKeys().get(0).equals(Measurement.create(8, 0.1923)));
        assertTrue(errors.get(1).getPointKeys().get(6).equals(Measurement.create(14, 0.1917)));
    }

    @Test
    public void verify_Fail_WhenIncrease_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.1911));
        values.add(Measurement.create(2, 0.1912));
        values.add(Measurement.create(3, 0.1913));
        values.add(Measurement.create(4, 0.1914));
        values.add(Measurement.create(5, 0.1915));
        values.add(Measurement.create(6, 0.1916));
        values.add(Measurement.create(7, 0.1913));
        values.add(Measurement.create(8, 0.1912));
        values.add(Measurement.create(9, 0.1924));
        values.add(Measurement.create(10, 0.1925));
        values.add(Measurement.create(11, 0.1926));
        values.add(Measurement.create(12, 0.1927));
        values.add(Measurement.create(13, 0.1928));
        values.add(Measurement.create(14, 0.1929));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(thirdCriteriaValidator.verify(chartSnapshot, errors));
        assertEquals(errors.size(), 2);

        assertEquals(errors.get(0).getPointKeys().size(), 6);
        assertEquals(errors.get(1).getPointKeys().size(), 7);

        assertTrue(errors.get(0).getPointKeys().get(0).equals(Measurement.create(1, 0.1911)));
        assertTrue(errors.get(0).getPointKeys().get(5).equals(Measurement.create(6, 0.1916)));
        assertTrue(errors.get(1).getPointKeys().get(0).equals(Measurement.create(8, 0.1912)));
        assertTrue(errors.get(1).getPointKeys().get(6).equals(Measurement.create(14, 0.1929)));
    }

    @Test
    public void verify_Fail_WhenIncreaseAndDecrease_Test() {
        List<SpecialReason<Integer>> errors = new ArrayList<>();
        List<Measurement<Integer, Double>> values = new LinkedList<>();
        values.add(Measurement.create(1, 0.1911));
        values.add(Measurement.create(2, 0.1912));
        values.add(Measurement.create(3, 0.1913));
        values.add(Measurement.create(4, 0.1914));
        values.add(Measurement.create(5, 0.1915));
        values.add(Measurement.create(6, 0.1916));
        values.add(Measurement.create(7, 0.1913));
        values.add(Measurement.create(8, 0.1925));
        values.add(Measurement.create(9, 0.1924));
        values.add(Measurement.create(10, 0.1923));
        values.add(Measurement.create(11, 0.1922));
        values.add(Measurement.create(12, 0.1921));
        values.add(Measurement.create(13, 0.1920));
        values.add(Measurement.create(14, 0.1919));
        ChartSnapshotBuilder<Integer> chartSnapshotBuilder = GeneralChartSnapshotBuilder.create();
        ChartSnapshot<Integer> chartSnapshot = chartSnapshotBuilder
                .withCentralLine(0.1924)
                .withUpperCentralLine(0.2133)
                .withLowerCentralLine(0.1715)
                .withChartValues(values)
                .build();

        assertFalse(thirdCriteriaValidator.verify(chartSnapshot, errors));
        assertEquals(errors.size(), 2);

        assertEquals(errors.get(0).getPointKeys().size(), 6);
        assertEquals(errors.get(1).getPointKeys().size(), 7);

        assertTrue(errors.get(0).getPointKeys().get(0).equals(Measurement.create(1, 0.1911)));
        assertTrue(errors.get(0).getPointKeys().get(5).equals(Measurement.create(6, 0.1916)));
        assertTrue(errors.get(1).getPointKeys().get(0).equals(Measurement.create(8, 0.1925)));
        assertTrue(errors.get(1).getPointKeys().get(6).equals(Measurement.create(14, 0.1919)));
    }

}
