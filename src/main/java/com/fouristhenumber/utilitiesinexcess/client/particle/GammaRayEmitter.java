package com.fouristhenumber.utilitiesinexcess.client.particle;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GammaRayEmitter {

    private static final int MAX_RAYS_PER_TICK = 12;
    private static final double DEFAULT_DENSE_GAP = 0.75D;
    private static final double DEFAULT_SPARSE_GAP = 2D;
    private static final double CONVERGING_SPEED = 0.175D;

    // Dense rays occur ~1 in (DENSE_RATIO_BASE / strength + DENSE_RATIO_OFFSET): denser mix at higher strength
    private static final double DENSE_RATIO_BASE = 22.0D;
    private static final double DENSE_RATIO_OFFSET = 3.0D;

    private GammaRayEmitter() {}

    public static void emit(World world, double cx, double cy, double cz, int strength, double speed, Random rand) {
        emit(world, cx, cy, cz, strength, speed, true, rand);
    }

    /**
     * Spawns a single ray that travels inward from a random nearby point and terminates at (cx, cy, cz).
     */
    public static void emitConverging(World world, double cx, double cy, double cz, Random rand) {
        double z = 2.0D * rand.nextDouble() - 1.0D;
        double theta = 2.0D * Math.PI * rand.nextDouble();
        double r = Math.sqrt(Math.max(0.0D, 1.0D - z * z));
        double ux = r * Math.cos(theta);
        double uy = z;
        double uz = r * Math.sin(theta);
        double len = 1.5D + rand.nextDouble() * 2.0D;

        Minecraft.getMinecraft().effectRenderer.addEffect(
            new EntityGammaRayFX(
                world,
                cx + ux * len,
                cy + uy * len,
                cz + uz * len,
                -ux,
                -uy,
                -uz,
                CONVERGING_SPEED,
                len,
                1.15D));
    }

    public static void emit(World world, double cx, double cy, double cz, int strength, double speed, boolean reverse,
        Random rand) {
        emit(world, cx, cy, cz, strength, speed, DEFAULT_DENSE_GAP, DEFAULT_SPARSE_GAP, reverse, rand);
    }

    /**
     * Emit a ray that flies from a given point to a random direction, and leaves behind a trail.
     * Each ray is randomly given the dense or the sparse trail; the fraction of dense rays grows with strength.
     *
     * @param denseGap  trail gap (ticks between dots, lower is denser) for the occasional dense ray
     * @param sparseGap trail gap for the common sparse rays
     */
    public static void emit(World world, double cx, double cy, double cz, int strength, double speed, double denseGap,
        double sparseGap, boolean reverse, Random rand) {
        if (strength <= 0) return;

        int rays = Math.min(MAX_RAYS_PER_TICK, strength);
        EffectRenderer er = Minecraft.getMinecraft().effectRenderer;

        double denseChance = 1.0D / (DENSE_RATIO_BASE / strength + DENSE_RATIO_OFFSET);

        for (int i = 0; i < rays; i++) {
            double z = 2.0D * rand.nextDouble() - 1.0D;
            double theta = 2.0D * Math.PI * rand.nextDouble();
            double r = Math.sqrt(Math.max(0.0D, 1.0D - z * z));
            double ux = r * Math.cos(theta);
            double uy = z;
            double uz = r * Math.sin(theta);
            double len = 1.5D + rand.nextDouble() * 2.0D;
            double gap = rand.nextDouble() < denseChance ? denseGap : sparseGap;

            er.addEffect(new EntityGammaRayFX(world, cx, cy, cz, ux, uy, uz, speed, len, gap));
            if (reverse) {
                er.addEffect(
                    new EntityGammaRayFX(
                        world,
                        cx + ux * len,
                        cy + uy * len,
                        cz + uz * len,
                        -ux,
                        -uy,
                        -uz,
                        speed,
                        len,
                        gap));
            }
        }
    }
}
