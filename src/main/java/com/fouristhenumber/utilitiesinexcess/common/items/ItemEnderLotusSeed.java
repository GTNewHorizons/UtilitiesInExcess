package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockEnderLotus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnderLotusSeed extends Item implements IPlantable {

    private final Block cropBlock;

    public ItemEnderLotusSeed(Block cropBlock) {
        super();
        this.cropBlock = cropBlock;
        this.setUnlocalizedName("ender_lotus_seed");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, net.minecraft.world.World world, int x, int y, int z,
        int side, float hitX, float hitY, float hitZ) {
        if (side != 1) return false;
        Block block = world.getBlock(x, y, z);
        if (!((BlockEnderLotus) cropBlock).canPlaceBlockOn(block)) {
            return false;
        }
        if (world.isAirBlock(x, y + 1, z)) {
            world.setBlock(x, y + 1, z, cropBlock, 0, 2);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg) {
        this.itemIcon = reg.registerIcon("utilitiesinexcess:ender_lotus_seed");
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Cave;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z) {
        return cropBlock;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
        return 0;
    }
}
