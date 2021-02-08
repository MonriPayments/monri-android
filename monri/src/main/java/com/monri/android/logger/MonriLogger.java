package com.monri.android.logger;

/**
 * Created by jasminsuljic on 2019-12-13.
 * MonriAndroid
 */
public interface MonriLogger {

    void info(String message);

    void trace(String message);

    void warn(String message);

    void error(String message);

    void fatal(String message);
}
