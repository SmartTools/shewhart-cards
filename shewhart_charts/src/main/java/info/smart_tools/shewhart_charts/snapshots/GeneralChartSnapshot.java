package info.smart_tools.shewhart_charts.snapshots;

import info.smart_tools.shewhart_charts.utils.Measurement;
import info.smart_tools.shewhart_charts.utils.ValidationUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkControlLines;
import static info.smart_tools.shewhart_charts.utils.ValidationUtils.checkOnNullOrEmpty;

public class GeneralChartSnapshot<TKey extends Comparable<TKey>> implements ChartSnapshot<TKey> {
    private double centralLine;
    private double upperCentralLine;
    private double lowerCentralLine;
    private AbstractMap<TKey, Measurement<TKey, Double>> chartValues;

    private GeneralChartSnapshot(
            double centralLine,
            double upperCentralLine,
            double lowerCentralLine,
            List<Measurement<TKey, Double>> chartValues
    ) {
        this.centralLine = centralLine;
        this.upperCentralLine = upperCentralLine;
        this.lowerCentralLine = lowerCentralLine;
        this.chartValues = new TreeMap<>();
        for (Measurement<TKey, Double> value : chartValues) {
            this.chartValues.put(value.getKey(), value);
        }
    }

    public static <TKey extends Comparable<TKey>> GeneralChartSnapshot<TKey> create(
            double centralLine,
            double upperCentralLine,
            double lowerCentralLine,
            List<Measurement<TKey, Double>> chartValues
    ) throws IllegalArgumentException {
        checkControlLines(centralLine, upperCentralLine, lowerCentralLine);
        checkOnNullOrEmpty(chartValues, "The chart values");

        return new GeneralChartSnapshot<>(centralLine, upperCentralLine, lowerCentralLine, chartValues);
    }

    public List<Measurement<TKey, Double>> getChartValues() {
        return new ArrayList<>(chartValues.values());
    }

    @Override
    public Double getCentralLine() {
        return centralLine;
    }

    @Override
    public Double getUpperCentralLine() {
        return upperCentralLine;
    }

    @Override
    public Double getLowerCentralLine() {
        return lowerCentralLine;
    }

}
