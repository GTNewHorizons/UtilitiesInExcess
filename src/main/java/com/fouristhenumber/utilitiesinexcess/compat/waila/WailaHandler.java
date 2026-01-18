package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.CommonProxy;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderQuarry;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;

/**
 * Registered in {@link CommonProxy} via IMC.
 */
public class WailaHandler implements IWailaDataProvider {

    public static void callbackRegister(IWailaRegistrar registrar) {
        WailaHandler instance = new WailaHandler();
        registrar.registerBodyProvider(instance, TileEntityEnderQuarry.class);
        registrar.registerNBTProvider(instance, TileEntityEnderQuarry.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof TileEntityEnderQuarry) {
            TileEntityEnderQuarry.QuarryWorkState state = TileEntityEnderQuarry.QuarryWorkState.VALUES[accessor
                .getNBTData()
                .getInteger("state")];
            currenttip.add(
                String.format(
                    LangUtil.instance.translate("uie.quarry.waila.state"),
                    LangUtil.instance.translate(state.localKey)));
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (te != null) {
            te.writeToNBT(tag);
        }
        return tag;
    }
}
