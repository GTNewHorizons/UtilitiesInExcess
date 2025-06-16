package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
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
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_,
        float p_77648_8_, float p_77648_9_, float p_77648_10_) {
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

        return super.onItemUse(itemStack, player, world, x, y, z, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_);
    }
}
