package info.smart_tools.shewhart_charts.utils;

import java.util.List;

public class ListUtils {
    public static <T> void copy(List<T> dest, List<T> src, int startIndex, int length) {
        if (dest.isEmpty())
            for (int i = 0; i < length; ++i)
                dest.add(i, src.get(i));
        else
            for (int i = 0; i < length; ++i)
                dest.set(i, src.get(i + startIndex));
    }
}
