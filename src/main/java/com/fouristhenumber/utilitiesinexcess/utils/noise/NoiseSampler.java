package com.fouristhenumber.utilitiesinexcess.utils.noise;

/// Something that samples noise for worldgen.
public interface NoiseSampler {

    double sample(double x, double y);

    double sample(double x, double y, double z);
}
