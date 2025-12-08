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
        ForgeDirection yaw = ForgeDirection.NORTH; // Default yaw

        // Get the horizontal 0-3 value from player's yaw
        int yawByte = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        // --- Calculate Horizontal Yaw Direction ---
        switch (yawByte) {
            case 0:
                yaw = ForgeDirection.NORTH; // Your code had NORTH
                break;
            case 1:
                yaw = ForgeDirection.EAST;
                break;
            case 2:
                yaw = ForgeDirection.SOUTH; // Your code had SOUTH
                break;
            case 3:
                yaw = ForgeDirection.WEST;
                break;
        }

        // --- Calculate 6-way Facing Direction ---
        float pitch = placer.rotationPitch;
        if (pitch > 55) {
            facing = ForgeDirection.UP;
            // 'yaw' is already set from above
        } else if (pitch < -45) {
            facing = ForgeDirection.DOWN;
            // 'yaw' is already set from above
        } else {
            // If facing is horizontal, the 'facing' and 'yaw' are the same
            facing = yaw;
        }

        return new FacingRotation(facing, yaw);
    }
}
