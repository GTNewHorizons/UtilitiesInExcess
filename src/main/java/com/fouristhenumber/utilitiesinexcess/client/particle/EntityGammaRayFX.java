package com.fouristhenumber.utilitiesinexcess.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityGammaRayFX extends EntityFX {

    private static final int TRAIL_AGE = 9;
    private static final double MIN_TRAIL_GAP = 0.1D;

    private final double dirX, dirY, dirZ;
    private final double speed;
    private final double trailGap;
    private double trailTimer;

    public EntityGammaRayFX(World world, double x, double y, double z, double dirX, double dirY, double dirZ,
        double speed, double travelDistance, double trailGap) {
        super(world, x, y, z);
        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;
        this.speed = speed;
        this.trailGap = Math.max(MIN_TRAIL_GAP, trailGap);
        this.particleGravity = 0.0F;
        this.noClip = true;
        setParticleTextureIndex(0);
        setRBGColorF(0.8F, 0.85F, 0.95F);
        this.particleMaxAge = Math.max(1, (int) Math.ceil(travelDistance / Math.max(speed, 1.0E-4D)));
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            setDead();
            return;
        }

        float progress = (float) this.particleAge / (float) this.particleMaxAge;
        this.particleAlpha = 1.0F - progress * 0.5F;

        // speed == 0 marks a stationary trail dot; the guard also stops dots from spawning their own trails
        if (this.speed > 0.0D) {
            // f(x) = 1 - x^5: full speed for most of the track, easing to a stop at the end
            double f = 1.0D - Math.pow(progress, 5);
            moveEntity(this.dirX * this.speed * f, this.dirY * this.speed * f, this.dirZ * this.speed * f);

            // Spread dots along the segment travelled this tick; a gap below 1 yields several dots per tick
            this.trailTimer += 1.0D;
            while (this.trailTimer >= this.trailGap) {
                this.trailTimer -= this.trailGap;
                double frac = 1.0D - this.trailTimer;
                spawnTrailDot(
                    this.prevPosX + (this.posX - this.prevPosX) * frac,
                    this.prevPosY + (this.posY - this.prevPosY) * frac,
                    this.prevPosZ + (this.posZ - this.prevPosZ) * frac);
            }
        }
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    private void spawnTrailDot(double x, double y, double z) {
        EntityGammaRayFX dot = new EntityGammaRayFX(this.worldObj, x, y, z, 0, 0, 0, 0, 0, 1);
        dot.particleMaxAge = TRAIL_AGE;
        dot.particleScale = this.particleScale * 0.5F;
        dot.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
        Minecraft.getMinecraft().effectRenderer.addEffect(dot);
    }
}
