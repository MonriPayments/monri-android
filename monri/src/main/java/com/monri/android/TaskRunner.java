package com.monri.android;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TaskRunner {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    public <Result> void executeAsync(Callable<Result> callable, ResultCallback<Result> callback) {
        executor.execute(() -> {
            final Result result;
            try {
                result = callable.call();
                handler.post(() -> {
                    callback.onSuccess(result);
                });
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}
