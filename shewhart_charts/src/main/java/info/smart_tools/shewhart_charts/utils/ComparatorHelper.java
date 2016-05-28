package info.smart_tools.shewhart_charts.utils;

import java.util.Comparator;

public class ComparatorHelper {
    public static Comparator<Double> getStandartComparator() {
        return (Double var1, Double var2) -> {
            if (var1 > var2)
                return 1;
            if (var1 < var2)
                return -1;
            else
                return 0;
        };
    }

    public static Comparator<Double> getInverseComparator() {
        return (Double var1, Double var2) -> {
            if (var1 > var2)
                return -1;
            if (var1 < var2)
                return 1;
            else
                return 0;
        };
    }
}
