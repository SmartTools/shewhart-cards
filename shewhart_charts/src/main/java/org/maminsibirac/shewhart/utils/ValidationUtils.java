package org.maminsibirac.shewhart.utils;

import java.util.Collection;
import java.util.Map;

public class ValidationUtils {
    public static final String NULL_ERROR_MSG = "should not be a null!";
    public static final String NULL_OR_EMPTY_ERROR_MSG = "should not be a null or empty!";

    public static void checkOnNull(Object obj, String message) throws IllegalArgumentException {
        if (obj == null)
            throw new IllegalArgumentException(message);
    }

    public static void checkOnNullOrEmpty(String value, String message) throws IllegalArgumentException {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException(message);
    }

    public static void checkOnNullOrEmpty(Collection value, String message) throws IllegalArgumentException {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException(message);
    }

    public static void checkOnNullOrEmpty(Map value, String message) throws IllegalArgumentException {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException(message);
    }

    public static void checkControlLines(
            double centralLine,
            double upperCentralLine,
            double lowerCentralLine
    ) throws IllegalArgumentException{
        if (centralLine == upperCentralLine || centralLine == lowerCentralLine)
            throw new IllegalArgumentException("The central line should not be an equals " +
                    "the upper central line or the lower central line!");
        if (centralLine > upperCentralLine || centralLine < lowerCentralLine)
            throw new IllegalArgumentException("The central line should be greater than the lower central line " +
                    "and less than the upper central line!");
        if (upperCentralLine <= lowerCentralLine)
            throw new IllegalArgumentException("The upper central line should be greater than the lower central line!");
    }


}
