package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;

public class BlockFloating extends Block {

    public BlockFloating() {
        super(Material.rock);
        setBlockName("floating_block");
        setBlockTextureName("utilitiesinexcess:floating_block");
        setHardness(0.0F);
        setResistance(7.0F);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return true;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if (player != null) {
            EntityItem entityItem = player.dropItem(ModBlocks.FLOATING_BLOCK.getItem(), 1);
            entityItem.delayBeforeCanPickup = 0;
        } else {
            super.harvestBlock(world, player, x, y, z, meta);
        }
    }

    public static class ItemBlockFloating extends ItemBlock {

        public ItemBlockFloating(Block block) {
            super(block);
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
            if (world.isRemote) {
                return stack;
            }

            MovingObjectPosition target = getMovingObjectPositionFromPlayer(world, player, true);
            if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                Vec3 lookVec = player.getLookVec();
                Vec3 eyePos = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);

                for (int i = 1; i < 2; i++) {
                    Vec3 targetVec = eyePos.addVector(lookVec.xCoord * i, lookVec.yCoord * i, lookVec.zCoord * i);
                    int bx = (int) Math.floor(targetVec.xCoord);
                    int by = (int) Math.floor(targetVec.yCoord);
                    int bz = (int) Math.floor(targetVec.zCoord);

                    Block block = world.getBlock(bx, by, bz);
                    if (world.isAirBlock(bx, by, bz) || block == Blocks.water || block == Blocks.flowing_water) {
                        if (world.canPlaceEntityOnSide(this.field_150939_a, bx, by, bz, false, 0, player, stack)) {
                            world.setBlock(bx, by, bz, this.field_150939_a, 0, 3);

                            if (!player.capabilities.isCreativeMode) {
                                stack.stackSize--;
                            }
                        }
                        break;
                    }
                }
            }

            return stack;
        }
    }
}
