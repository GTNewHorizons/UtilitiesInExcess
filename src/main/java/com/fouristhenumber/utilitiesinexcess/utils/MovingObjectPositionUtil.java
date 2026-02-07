package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class MovingObjectPositionUtil {

    public static void TranslateMovingObjectPoistionToLocation(MovingObjectPosition movingObjectPosition,
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
}
