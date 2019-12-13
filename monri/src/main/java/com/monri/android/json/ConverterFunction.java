package com.monri.android.json;

/**
 * Created by jasminsuljic on 2019-12-05.
 * MonriAndroid
 */
public interface ConverterFunction<K, T> {
    T create(K k);
}
