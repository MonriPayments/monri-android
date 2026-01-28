package com.monri.android.model;

public class ScanDocKeypoint {
    private final double x;
    private final double y;

    public ScanDocKeypoint(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
