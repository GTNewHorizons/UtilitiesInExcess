package com.fouristhenumber.utilitiesinexcess.utils.noise;

/// A [NoiseSampler] wrapper that scales the coordinates and result of another [NoiseSampler].
public class ScaledNoise implements NoiseSampler {

    private final NoiseSampler base;
    private final double scaleX;
    private final double scaleY;
    private final double scaleZ;
    private final double amplitude;
    private final double offset;

    public ScaledNoise(NoiseSampler base, double scaleX, double scaleY, double scaleZ, double amplitude,
        double offset) {
        this.base = base;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.amplitude = amplitude;
        this.offset = offset;
    }

    public ScaledNoise(NoiseSampler base, double scaleX, double scaleY, double scaleZ) {
        this.base = base;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.amplitude = 1d;
        this.offset = 0d;
    }

    public ScaledNoise(NoiseSampler base, double scale) {
        this(base, scale, scale, scale);
    }

    @Override
    public double sample(double x, double y) {
        return base.sample(x * scaleX, y * scaleY) * amplitude + offset;
    }

    @Override
    public double sample(double x, double y, double z) {
        return base.sample(x * scaleX, y * scaleY, z * scaleZ) * amplitude + offset;
    }
}
