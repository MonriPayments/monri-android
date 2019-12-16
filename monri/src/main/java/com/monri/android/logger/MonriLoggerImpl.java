package com.monri.android.logger;

import android.util.Log;

/**
 * Created by jasminsuljic on 2019-12-13.
 * MonriAndroid
 */
class MonriLoggerImpl implements MonriLogger {

    private final String tag;

    MonriLoggerImpl(Class targetClass) {
        this.tag = targetClass.getSimpleName().substring(0, 24);
    }

    @Override
    public void info(String message, Object... args) {
        Log.i(tag, String.format(message, args));
    }

    @Override
    public void trace(String message, Object... args) {
        Log.i(tag, String.format(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        Log.w(tag, String.format(message, args));
    }

    @Override
    public void error(String message, Object... args) {
        Log.e(tag, String.format(message, args));
    }

    @Override
    public void fatal(String message, Object... args) {
        Log.wtf(tag, String.format(message, args));
    }
}
