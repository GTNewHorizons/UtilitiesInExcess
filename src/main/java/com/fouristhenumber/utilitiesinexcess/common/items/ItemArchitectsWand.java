package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.renderers.WireframeRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ArchitectsWandUtils;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArchitectsWand extends Item {

    public int buildLimit;

    public ItemArchitectsWand(int buildLimit) {
        super();
        this.buildLimit = buildLimit;
        setUnlocalizedName("architects_wand");
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(
            EnumChatFormatting.AQUA
                + StatCollector.translateToLocalFormatted("tooltip.architects_wand.1", this.buildLimit));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote || !(entity instanceof EntityPlayer player)) {
            return;
        }

        if (!isSelected) return;

        MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;

        // Check if player is looking at a block.
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            WireframeRenderer.clearCandidatePositions();
            return;
        }

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;
        int side = mop.sideHit;
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG
                .warn("Architect wand onUpdate was called with invalid facing direction: {}", forgeSide);
            return;
        }

        Block blockToPlace = world.getBlock(x, y, z);
        if (blockToPlace == null) return;
        int metaToPlace = world.getBlockMetadata(x, y, z);
        ItemStack itemStack = new ItemStack(Item.getItemFromBlock(blockToPlace), 1, metaToPlace);

        int inventoryBlockCount = ArchitectsWandUtils.countItemInInventory(player, itemStack);
        if (!player.capabilities.isCreativeMode && inventoryBlockCount == 0) {
            WireframeRenderer.clearCandidatePositions();
            return;
        }
        int placeCount = player.capabilities.isCreativeMode ? this.buildLimit
            : Math.min(inventoryBlockCount, this.buildLimit);

        Set<BlockPos> blocksToPlace = ArchitectsWandUtils
            .findAdjacentBlocks(world, blockToPlace, metaToPlace, placeCount, forgeSide, new BlockPos(x, y, z));
        WireframeRenderer.clearCandidatePositions();
        for (BlockPos pos : blocksToPlace) {
            WireframeRenderer.addCandidatePosition(pos.offset(forgeSide.offsetX, forgeSide.offsetY, forgeSide.offsetZ));
        }

    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // TODO: Prevent player from placing blocks into themself / other entities?
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG
                .warn("Architects wand onItemUse was called with invalid facing direction: {}", forgeSide);
            return false;
        }

        Block blockToPlace = world.getBlock(x, y, z);
        if (blockToPlace == null) return false;
        int metaToPlace = world.getBlockMetadata(x, y, z);
        ItemStack itemStack = new ItemStack(Item.getItemFromBlock(blockToPlace), 1, metaToPlace);

        int inventoryBlockCount = ArchitectsWandUtils.countItemInInventory(player, itemStack);
        if (!player.capabilities.isCreativeMode && inventoryBlockCount == 0) return false;
        int placeCount = player.capabilities.isCreativeMode ? this.buildLimit
            : Math.min(inventoryBlockCount, this.buildLimit);

        Set<BlockPos> blocksToPlace = ArchitectsWandUtils
            .findAdjacentBlocks(world, blockToPlace, metaToPlace, placeCount, forgeSide, new BlockPos(x, y, z));
        for (BlockPos pos : blocksToPlace) {
            // TODO: Group these by a bigger number instead of decreasing by 1 every time.
            if (player.capabilities.isCreativeMode || ArchitectsWandUtils.decreaseFromInventory(player, itemStack)) {
                world.setBlock(
                    pos.x + forgeSide.offsetX,
                    pos.y + forgeSide.offsetY,
                    pos.z + forgeSide.offsetZ,
                    blockToPlace,
                    metaToPlace,
                    3);
            }
        }
        return true;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

}
