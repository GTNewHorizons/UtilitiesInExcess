package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockFloating extends Block {

    public BlockFloating() {
        super(Material.rock);
        setBlockName("floating_block");
        setBlockTextureName("utilitiesinexcess:floating_block");
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return true;
    }

    public static class ItemBlockFloating extends ItemBlock {

        public ItemBlockFloating(Block block) {
            super(block);
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
            if (!world.isRemote) {
                MovingObjectPosition target = getMovingObjectPositionFromPlayer(world, player, true);
                if (target == null || target.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
                    Vec3 lookVec = player.getLookVec();
                    Vec3 eyePos = Vec3
                        .createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);

                    for (int i = 1; i < 2; i++) {
                        Vec3 targetVec = eyePos.addVector(lookVec.xCoord * i, lookVec.yCoord * i, lookVec.zCoord * i);
                        int bx = (int) Math.floor(targetVec.xCoord);
                        int by = (int) Math.floor(targetVec.yCoord);
                        int bz = (int) Math.floor(targetVec.zCoord);

                        if (world.isAirBlock(bx, by, bz)) {
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
            }

            return stack;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("tile.floating_block.desc"));
            super.addInformation(stack, player, tooltip, p_77624_4_);
        }
    }
}
