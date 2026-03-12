package com.fouristhenumber.utilitiesinexcess.common.events;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class WateringCanEvent extends WorldEvent {

    public final BlockPos wateredBlock;
    public final BlockPos originBlock;
    public final int wateringCanTier;

    /**
     * {@link WateringCanEvent} is fired when a block is fertilized using a watering can.
     * <br>
     * {@link #wateredBlock} contains the {@link BlockPos} of the block being fertilized. <br>
     * {@link #originBlock} contains the {@link BlockPos} of the block which was clicked. <br>
     * {@link #wateringCanTier} contains the tier of the watering can. <br>
     * <br>
     * This event is not {@link cpw.mods.fml.common.eventhandler.Cancelable}. <br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
     **/
    public WateringCanEvent(World world, BlockPos wateredBlock, BlockPos originBlock, int wateringCanTier) {
        super(world);
        this.wateredBlock = wateredBlock;
        this.originBlock = originBlock;
        this.wateringCanTier = wateringCanTier;
    }
}
