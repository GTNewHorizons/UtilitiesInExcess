package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityBaseGeneratorWithItemFuel;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class GeneratorDataProvider implements IWailaDataProvider {

    public static final GeneratorDataProvider INSTANCE = new GeneratorDataProvider();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound fuel = (NBTTagCompound) accessor.getNBTData()
            .getTag("fuel");
        if (fuel == null) {
            return currentTip;
        }
        ItemStack fuelStack = ItemStack.loadItemStackFromNBT(fuel);
        if (fuelStack == null) {
            return currentTip;
        }
        currentTip.add(
            StatCollector.translateToLocalFormatted(
                "uie.generators.waila.fuel",
                fuelStack.stackSize + "x " + fuelStack.getDisplayName()));

        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (!(te instanceof TileEntityBaseGeneratorWithItemFuel)) {
            return tag;
        }
        ItemStack stack = ((TileEntityBaseGeneratorWithItemFuel) te).getStackInSlot(0);
        if (stack == null) {
            return tag;
        }
        tag.setTag("fuel", stack.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    // Snubs
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
