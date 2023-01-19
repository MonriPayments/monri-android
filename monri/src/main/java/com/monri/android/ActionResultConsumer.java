package com.monri.android;

@FunctionalInterface
public interface ActionResultConsumer<T> {
    void accept(ActionResult<T> result);
}
