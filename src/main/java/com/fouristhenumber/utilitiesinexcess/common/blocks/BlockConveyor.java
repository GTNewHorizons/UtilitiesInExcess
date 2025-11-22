package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityConveyor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockConveyor extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon belt_up, belt_down, belt_left, belt_right, blank;

    public BlockConveyor() {
        super(Material.piston);

        setBlockName("conveyor");
    }

    public static ForgeDirection getFacing(int meta) {
        return switch (meta) {
            case 0 -> ForgeDirection.SOUTH;
            case 1 -> ForgeDirection.WEST;
            case 2 -> ForgeDirection.NORTH;
            case 3 -> ForgeDirection.EAST;
            default -> ForgeDirection.UNKNOWN;
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        belt_up = reg.registerIcon("utilitiesinexcess:conveyor_belt_up");
        belt_down = reg.registerIcon("utilitiesinexcess:conveyor_belt_down");
        belt_left = reg.registerIcon("utilitiesinexcess:conveyor_belt_left");
        belt_right = reg.registerIcon("utilitiesinexcess:conveyor_belt_right");
        blank = reg.registerIcon("utilitiesinexcess:conveyor_belt_blank");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int s, int meta) {
        if (s != ForgeDirection.UP.ordinal()) return blank;

        return switch (getFacing(meta)) {
            case NORTH -> belt_up;
            case SOUTH -> belt_down;
            case WEST -> belt_left;
            case EAST -> belt_right;
            default -> null;
        };
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityConveyor();
    }

    public static class ItemBlockConveyor extends ItemBlock {

        public ItemBlockConveyor(Block block) {
            super(block);
        }

        @Override
        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
            int direction = Math.abs((int) ((((player.rotationYaw % 360) + 45f) / 90f + 4f) % 4f));

            if (!world.setBlock(x, y, z, field_150939_a, direction, 3)) {
                return false;
            }

            if (world.getBlock(x, y, z) == field_150939_a) {
                field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
                field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
            }

            return true;
        }
    }
}
