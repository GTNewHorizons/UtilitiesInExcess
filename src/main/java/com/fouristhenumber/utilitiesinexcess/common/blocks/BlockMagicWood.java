package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockMagicWood extends Block {

    public BlockMagicWood() {
        super(Material.wood);
        setBlockName("magic_wood");
        setBlockTextureName("utilitiesinexcess:magic_wood");

        setResistance(30.0f);
        setHardness(0.45f);
        setStepSound(soundTypeWood);
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        return 5.0f;
    }

    public static class ItemBlockMagicWood extends ItemBlock {

        public ItemBlockMagicWood(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocalFormatted("tile.magic_wood.desc"));
        }
    }
}
