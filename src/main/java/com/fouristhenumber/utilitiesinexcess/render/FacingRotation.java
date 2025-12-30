package com.fouristhenumber.utilitiesinexcess.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class FacingRotation {

    public final ForgeDirection facing;
    public final ForgeDirection yaw;

    public FacingRotation(ForgeDirection facing, ForgeDirection yaw) {
        this.facing = facing;
        this.yaw = yaw;
    }

    public static FacingRotation calculateFacingRotation(EntityLivingBase placer) {
        ForgeDirection facing;
        ForgeDirection yaw = ForgeDirection.NORTH;

        int yawByte = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        switch (yawByte) {
            case 0:
                yaw = ForgeDirection.NORTH;
                break;
            case 1:
                yaw = ForgeDirection.EAST;
                break;
            case 2:
                yaw = ForgeDirection.SOUTH;
                break;
            case 3:
                yaw = ForgeDirection.WEST;
                break;
        }

        float pitch = placer.rotationPitch;
        if (pitch > 55) {
            facing = ForgeDirection.UP;
        } else if (pitch < -45) {
            facing = ForgeDirection.DOWN;
        } else {
            facing = yaw;
        }

        return new FacingRotation(facing, yaw);
    }
}
