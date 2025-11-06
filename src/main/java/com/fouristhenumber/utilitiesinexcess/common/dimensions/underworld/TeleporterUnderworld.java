package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/// Hacky Teleporter implementation that moves the player to a specific location instead of using the portal system.
public class TeleporterUnderworld extends Teleporter {

    private final int x;
    private final int y;
    private final int z;

    public TeleporterUnderworld(WorldServer world, int x, int y, int z) {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeInPortal(Entity entity, double p_77185_2_, double p_77185_4_, double p_77185_6_, float p_77185_8_) {
        entity.setLocationAndAngles(x + 0.5, y, z + 0.5, entity.rotationYaw, 0.0F);
        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double p_77184_2_, double p_77184_4_, double p_77184_6_, float p_77184_8_) {
        entity.setLocationAndAngles(x + 0.5, y, z + 0.5, entity.rotationYaw, 0.0F);
        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
        return true;
    }

    @Override
    public boolean makePortal(Entity p_85188_1_) {
        return true;
    }
}
