package com.monri.android.logger;

/**
 * Created by jasminsuljic on 2019-12-13.
 * MonriAndroid
 */
public class MonriLoggerFactory {
    public static MonriLogger get(String targetClass) {
        return new MonriLoggerImpl(targetClass);
    }
}
