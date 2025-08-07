package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

// TODO: Add new features to the reversing hoe
public class ItemReversingHoe extends ItemHoe {

    public ItemReversingHoe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:reversing_hoe");
        setUnlocalizedName("reversing_hoe");
        setMaxDamage(0);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.dirt) {
            world.setBlock(x, y, z, Blocks.grass);
            return true;
        } else if (block == Blocks.cobblestone) {
            world.setBlock(x, y, z, Blocks.stone);
            return true;
        } else if (block == Blocks.wheat) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta > 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta - 1, 3);
                return true;
            }
            return false;
        }

        return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.reversing_hoe.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.reversing_hoe.desc.2"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
    //
}
