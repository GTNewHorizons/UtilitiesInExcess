package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class RainMufflerDataProvider implements IWailaDataProvider {

    public static final RainMufflerDataProvider INSTANCE = new RainMufflerDataProvider();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        NBTTagCompound playerNBT = player.getEntityData();
        NBTTagCompound persistentNBT = playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (persistentNBT.getBoolean(TileEntityRainMuffler.NBT_RAIN_MUFFLED)) {
            currentTip.add(StatCollector.translateToLocal("tile.rain_muffler.waila.global_enabled"));
        } else {
            currentTip.add(StatCollector.translateToLocal("tile.rain_muffler.waila.global_disabled"));
        }

        return currentTip;
    }

    // Snubs
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return tag;
    }

    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }

    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }
}
