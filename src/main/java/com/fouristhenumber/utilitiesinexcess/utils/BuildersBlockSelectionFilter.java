package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemScribe;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import gregtech.api.items.MetaGeneratedTool;
import gregtech.common.tools.ToolTrowel;
import xonin.backhand.api.core.BackhandUtils;

public class BuildersBlockSelectionFilter {

    private final List<ItemStack> validBlocks;
    private final ItemStack backhand;
    private final ItemStack lookAtBlock;

    private final boolean isCopyMode;

    public BuildersBlockSelectionFilter(EntityPlayer player, World world, MovingObjectPosition movingObjectPosition) {
        this.validBlocks = new ArrayList<>();
        backhand = Mods.Backhand.isLoaded() ? BackhandUtils.getOffhandItem(player) : null;
        lookAtBlock = getBlockByLocation(world, movingObjectPosition, player);
        isCopyMode = lookAtBlock != null && isScribe(backhand);

        // No logic is executed if we don't look at any block, no need to bother checking other cases
        if (lookAtBlock == null) return;

        this.validBlocks.add(lookAtBlock); // Clicked block is always valid

        if (isValidBlock(backhand)) {
            this.validBlocks.add(backhand.copy());
        } else if (isTrowel(backhand)) {
            this.validBlocks.addAll(hotbarBlocks(player));
        }
    }

    /**
     * Generates a list of blocks to build with
     */
    public List<ItemStack> generatePalette(EntityPlayer player) {
        if (lookAtBlock == null) return Collections.emptyList();
        if (isValidBlock(backhand)) return Collections.singletonList(backhand);
        if (isTrowel(backhand)) return hotbarBlocks(player);

        return Collections.singletonList(lookAtBlock);
    }

    /**
     * Checks if the given ItemStack matches any of the valid blocks in the filter
     */
    public boolean matches(ItemStack other) {
        if (other == null) return false;

        if (isCopyMode) return true;

        return this.validBlocks.stream()
            .anyMatch(validBlock -> ItemUtil.areStacksEqual(validBlock, other));
    }

    /**
     * Whether the fill spans the whole surface regardless of what it is made of, copying each position's
     * own block rather than drawing from a palette.
     */
    public boolean isCopyMode() {
        return isCopyMode;
    }

    public static boolean isTrowel(@Nullable ItemStack stack) {
        if (stack == null) return false;

        if (Mods.GregTech.isLoaded() && stack.getItem() instanceof MetaGeneratedTool metaGeneratedTool)
            return metaGeneratedTool.getToolStats(stack) instanceof ToolTrowel;

        return false;
    }

    public static boolean isScribe(@Nullable ItemStack stack) {
        if (stack == null) return false;

        return stack.getItem() instanceof ItemScribe;
    }

    private static boolean isValidBlock(@Nullable ItemStack stack) {
        return (stack != null && stack.getItem() instanceof ItemBlock);
    }

    private static List<ItemStack> hotbarBlocks(EntityPlayer player) {
        List<ItemStack> candidates = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (i == player.inventory.currentItem) {
                continue;
            }
            ItemStack item = player.inventory.mainInventory[i];
            if (!isValidBlock(item)) {
                continue;
            }
            candidates.add(item.copy());
        }
        return candidates;
    }

    public static ItemStack getBlockByLocation(World world, MovingObjectPosition movingObjectPosition,
        EntityPlayer player) {

        BlockPos blockPos = new BlockPos(
            movingObjectPosition.blockX,
            movingObjectPosition.blockY,
            movingObjectPosition.blockZ);

        Block block = world.getBlock(blockPos.x, blockPos.y, blockPos.z);
        if (block == null) {
            return null;
        }
        return block.getPickBlock(movingObjectPosition, world, blockPos.x, blockPos.y, blockPos.z, player);
    }
}
