package com.fouristhenumber.utilitiesinexcess.compat.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColoredCTM;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColoredWithLight;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;

public class ColoredBlocksDataProvider implements IWailaDataProvider {

    public static final ColoredBlocksDataProvider INSTANCE = new ColoredBlocksDataProvider();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        currentTip.add(SpecialChars.getRenderString("waila.uie.coloredblock"));

        if (BlockColored.getExtraMetaBit(accessor.getMetadata()) > 0) {
            if (accessor.getBlock() instanceof BlockColoredWithLight) {
                currentTip.add(StatCollector.translateToLocal("uie.desc.block_colored_light"));
            } else if (accessor.getBlock() instanceof BlockColoredCTM) {
                currentTip.add(StatCollector.translateToLocal("uie.desc.block_colored_ctm"));
            }
        }

        return currentTip;
    }

    // Stubs
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
