package com.monri.android;

@FunctionalInterface
public interface ActionResultConsumer<T> {
    void accept(T result, Throwable cause);
}
