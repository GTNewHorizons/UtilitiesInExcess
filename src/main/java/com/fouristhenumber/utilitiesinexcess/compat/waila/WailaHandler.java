package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.CommonProxy;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityVoidMarker;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityVoidQuarry;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.cbcore.LangUtil;

/**
 * Registered in {@link CommonProxy} via IMC.
 */
public class WailaHandler implements IWailaDataProvider {

    public static void callbackRegister(IWailaRegistrar registrar) {
        WailaHandler instance = new WailaHandler();
        registrar.registerBodyProvider(instance, TileEntityVoidQuarry.class);
        registrar.registerNBTProvider(instance, TileEntityVoidQuarry.class);
        registrar.registerBodyProvider(instance, TileEntityVoidMarker.class);
        registrar.registerNBTProvider(instance, TileEntityVoidMarker.class);
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
        if (accessor.getTileEntity() instanceof TileEntityVoidQuarry) {
            NBTTagCompound nbt = accessor.getNBTData();
            int estimatedSecondsLeft = nbt.getInteger("estSecondsLeft");
            if (estimatedSecondsLeft > 0) currenttip.add(
                SpecialChars.getRenderString(
                    "waila.uie.progress",
                    String.valueOf(nbt.getLong("brokenBlocks")),
                    String.valueOf(nbt.getLong("estBlocks")),
                    // Can't pass the time format string via the localization system since it seems to remove the
                    // leading zero formatting
                    LangUtil.instance.translate(
                        "uie.quarry.waila.timeleft",
                        String.format(
                            "%02d:%02d:%02d",
                            estimatedSecondsLeft / 3600,
                            (estimatedSecondsLeft % 3600) / 60,
                            (estimatedSecondsLeft % 60)))));

            TileEntityVoidQuarry.QuarryWorkState state = TileEntityVoidQuarry.QuarryWorkState
                .valueOf(nbt.getString("state"));
            currenttip.add(
                LangUtil.instance.translate("uie.quarry.waila.state", LangUtil.instance.translate(state.localKey)));
        } else if (accessor.getTileEntity() instanceof TileEntityVoidMarker) {
            NBTTagCompound nbt = accessor.getNBTData();
            TileEntityVoidMarker.MarkerOperationMode mode = TileEntityVoidMarker.MarkerOperationMode
                .valueOf(nbt.getString("mode"));
            currenttip.add(
                LangUtil.instance
                    .translate("uie.quarry.marker.waila.state", LangUtil.instance.translate(mode.localKey)));
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
