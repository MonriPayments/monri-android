package com.monri.android.logger;

import android.util.Log;

/**
 * Created by jasminsuljic on 2019-12-13.
 * MonriAndroid
 */
class MonriLoggerImpl implements MonriLogger {

    private final String tag;

    MonriLoggerImpl(String targetClass) {
        this.tag = targetClass.length() > 20 ? targetClass.substring(0, 20) : targetClass;
    }

    @Override
    public void info(String message) {
        tryLogAndContinue(() -> Log.i(tag, message));
    }

    @Override
    public void trace(String message) {
        tryLogAndContinue(() -> Log.i(tag, message));
    }

    private void tryLogAndContinue(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            Log.w("Logger", String.format("Logging message failed %s", e.getMessage()));
        }
    }

    @Override
    public void warn(String message) {
        tryLogAndContinue(() -> Log.w(tag, message));
    }

    @Override
    public void error(String message) {
        tryLogAndContinue(() -> Log.e(tag, message));
    }

    @Override
    public void fatal(String message) {
        tryLogAndContinue(() -> Log.wtf(tag, message));
    }
}
