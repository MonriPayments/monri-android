package com.monri.android;

import androidx.annotation.Nullable;

public final class ActionResult<T> {
    final T result;
    final Throwable throwable;

    public ActionResult(T result, Throwable throwable) {
        this.result = result;
        this.throwable = throwable;
    }

    @Nullable
    public T getResult() {
        return result;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isFailed() {
        return throwable != null;
    }

}
