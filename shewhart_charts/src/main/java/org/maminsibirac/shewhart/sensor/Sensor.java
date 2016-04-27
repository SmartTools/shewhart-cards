package org.maminsibirac.shewhart.sensor;

import java.io.Closeable;

/**
 *
 */
public interface Sensor<TData> extends Runnable, Closeable {
    void update(TData data);
    void report();
}
