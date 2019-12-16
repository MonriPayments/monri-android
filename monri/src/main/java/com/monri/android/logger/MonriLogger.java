package com.monri.android.logger;

/**
 * Created by jasminsuljic on 2019-12-13.
 * MonriAndroid
 */
public interface MonriLogger {

    void info(String message, Object... args);

    void trace(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void fatal(String message, Object... args);
}
