package com.monri.android.activity.fake;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Fake scheduler that instantly runs given Runnable/Callable.
 * */
public final class FakeScheduledExecutorService implements ScheduledExecutorService {

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long l, TimeUnit timeUnit) {
        runnable.run();
        return null;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long l, TimeUnit timeUnit) {
        try {
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long l, long l1, TimeUnit timeUnit) {
        runnable.run();
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long l, long l1, TimeUnit timeUnit) {
        runnable.run();
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        try {
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t) {
        runnable.run();
        return null;
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        runnable.run();
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
