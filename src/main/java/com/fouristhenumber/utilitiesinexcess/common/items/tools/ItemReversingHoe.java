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

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.ReversingHoeConfig;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;

// TODO: Add new features to the reversing hoe
public class ItemReversingHoe extends ItemHoe implements ITranslucentItem {

    public ItemReversingHoe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:reversing_hoe");
        setUnlocalizedName("reversing_hoe");
        if (ReversingHoeConfig.unbreakable) setMaxDamage(0);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        Block block = world.getBlock(x, y, z);
        // TODO: config for this
        if (block == Blocks.dirt) {
            world.setBlock(x, y, z, Blocks.grass);
            return true;
        } else if (block == Blocks.cobblestone) {
            world.setBlock(x, y, z, Blocks.stone);
            return true;
        } else if (block == ModBlocks.CURSED_EARTH.get() && CursedEarthConfig.enableBlessedEarth) {
            world.setBlock(x, y, z, ModBlocks.BLESSED_EARTH.get());
            return true;
        } else if (block == ModBlocks.BLESSED_EARTH.get() && CursedEarthConfig.enableCursedEarth) {
            world.setBlock(x, y, z, ModBlocks.CURSED_EARTH.get());
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
        if (ReversingHoeConfig.unbreakable)
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("item.unbreakable.desc"));
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (ReversingHoeConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (ReversingHoeConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (ReversingHoeConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
