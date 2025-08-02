package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;

public class TileEntityPureLove extends TileEntity {

    private static final long UPDATE_INTERVAL_IN_TICKS = 100; // 5 seconds
    private static final SelectNotInLove selector = new SelectNotInLove();

    @Override
    public void updateEntity() {
        if (this.worldObj != null && !this.worldObj.isRemote
            && this.worldObj.getTotalWorldTime() % UPDATE_INTERVAL_IN_TICKS == 0L) {
            int range = BlockConfig.pureLove.rangePureLove;
            AxisAlignedBB boundingBox = getBoundingBox(
                xCoord - range,
                yCoord - range,
                zCoord - range,
                xCoord + range,
                yCoord + range,
                zCoord + range);
            List<EntityAnimal> entityAnimals = worldObj
                .selectEntitiesWithinAABB(EntityAnimal.class, boundingBox, selector);
            for (EntityAnimal animal : entityAnimals) {
                animal.func_146082_f(null); // Put in love mode with no player responsible
            }
        }
    }

    private static class SelectNotInLove implements IEntitySelector {

        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof EntityAnimal animal && !animal.isInLove() && animal.getGrowingAge() == 0;
        }
    }
}
