package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class MovingObjectPositionUtil {

    public static void TranslateMovingObjectPositionToLocation(MovingObjectPosition movingObjectPosition,
        BlockPos location) {
        double offsetXn = movingObjectPosition.hitVec.xCoord - movingObjectPosition.blockX;
        double offsetYn = movingObjectPosition.hitVec.yCoord - movingObjectPosition.blockY;
        double offsetZn = movingObjectPosition.hitVec.zCoord - movingObjectPosition.blockZ;

        movingObjectPosition.blockX = location.x;
        movingObjectPosition.blockY = location.y;
        movingObjectPosition.blockZ = location.z;

        movingObjectPosition.hitVec = Vec3
            .createVectorHelper(location.x + offsetXn, location.y + offsetYn, location.z + offsetZn);
    }

    public static MovingObjectPosition copy(MovingObjectPosition movingObjectPosition) {
        return new MovingObjectPosition(
            movingObjectPosition.blockX,
            movingObjectPosition.blockY,
            movingObjectPosition.blockZ,
            movingObjectPosition.sideHit,
            Vec3.createVectorHelper(
                movingObjectPosition.hitVec.xCoord,
                movingObjectPosition.hitVec.yCoord,
                movingObjectPosition.hitVec.zCoord));
    }

    /** The block on the other side of the hit face. */
    public static BlockPos targetPos(MovingObjectPosition movingObjectPosition) {
        ForgeDirection side = ForgeDirection.getOrientation(movingObjectPosition.sideHit);
        return new BlockPos(
            movingObjectPosition.blockX + side.offsetX,
            movingObjectPosition.blockY + side.offsetY,
            movingObjectPosition.blockZ + side.offsetZ);
    }

    public static BlockPos blockPos(MovingObjectPosition movingObjectPosition) {
        return new BlockPos(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
    }

    /** Where on the block the ray landed, 0..1, as onItemUse and ItemBlock take it. */
    public static float hitX(MovingObjectPosition movingObjectPosition) {
        return (float) (movingObjectPosition.hitVec.xCoord - movingObjectPosition.blockX);
    }

    public static float hitY(MovingObjectPosition movingObjectPosition) {
        return (float) (movingObjectPosition.hitVec.yCoord - movingObjectPosition.blockY);
    }

    public static float hitZ(MovingObjectPosition movingObjectPosition) {
        return (float) (movingObjectPosition.hitVec.zCoord - movingObjectPosition.blockZ);
    }
}
