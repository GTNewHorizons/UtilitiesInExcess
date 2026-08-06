package com.fouristhenumber.utilitiesinexcess.utils;

public class Tuple<X, Y> {

    public final X first;
    public final Y second;

    public Tuple(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    public X getKey() {
        return this.first;
    }

    public Y getValue() {
        return this.second;
    }
}
