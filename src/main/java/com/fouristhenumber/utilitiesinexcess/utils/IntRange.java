package com.fouristhenumber.utilitiesinexcess.utils;

public class IntRange {

    public final int lower, upper;

    public IntRange(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public int lerp(double k) {
        return lower + (int) ((upper - lower) * k);
    }
}
