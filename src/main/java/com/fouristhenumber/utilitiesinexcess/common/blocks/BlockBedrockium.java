package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBedrockium extends Block {

    public BlockBedrockium() {
        super(Material.iron);
        setBlockName("bedrockium_block");
        setBlockTextureName("utilitiesinexcess:bedrockium_block");
        setHardness(500F);
        setResistance(10000F);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    public static class ItemBlockBedrockium extends ItemBlock {

        public ItemBlockBedrockium(Block block) {
            super(block);
        }

        @Override
        public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_) {
            if (entityIn instanceof EntityLivingBase entity) {
                entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 0, 3, true));
            }
        }
    }
}
