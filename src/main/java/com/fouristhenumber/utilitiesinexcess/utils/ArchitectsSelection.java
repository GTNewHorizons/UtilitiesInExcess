package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import gregtech.api.items.MetaGeneratedTool;
import gregtech.common.tools.ToolTrowel;
import xonin.backhand.api.core.BackhandUtils;

public class ArchitectsSelection {

    private final Set<ItemStack> validBlocks;
    private final ItemStack backhand;
    private final ItemStack lookAtBlock;

    public ArchitectsSelection(EntityPlayer player, World world, MovingObjectPosition movingObjectPosition) {
        this.validBlocks = new HashSet<>();
        backhand = Mods.Backhand.isLoaded() ? BackhandUtils.getOffhandItem(player) : null;
        lookAtBlock = getBlockByLocation(world, movingObjectPosition, player);

        // No logic is executed if we don't look at any block, no need to bother checking other cases
        if (lookAtBlock == null) {
            return;
        }

        this.validBlocks.add(lookAtBlock); // Clicked block is always valid

        if (backhand == null) {
            return;
        }
        if (isValidBlock(backhand)) {
            this.validBlocks.add(backhand.copy());
            return;
        }
        if (isTrowel(backhand)) {
            this.validBlocks.addAll(hotbarBlocks(player));
        }
    }

    /**
     *
     * @param player
     * @return Always a valid block list or null
     */
    public List<ItemStack> blockToPlace(EntityPlayer player) {

        if (backhand == null) {
            return Collections.singletonList((lookAtBlock));
        }
        if (isValidBlock(backhand)) {
            return Collections.singletonList(backhand);
        } else if (isTrowel(backhand)) {
            return hotbarBlocks(player);
        } else {
            return Collections.singletonList(lookAtBlock);
        }
    }

    public int maxPlaceCount(EntityPlayer player, int wandLimit) {
        if (player.capabilities.isCreativeMode) return wandLimit;
        int count = 0;
        for (ItemStack block : blockToPlace(player)) {
            count += ArchitectsWandUtils.countItemInInventory(player, block);
        }
        return Math.min(count, wandLimit);
    }

    public boolean matches(ItemStack other) {
        if (other == null) return false;
        return this.validBlocks.stream()
            .anyMatch(
                validBlock -> validBlock.getItem() == other.getItem()
                    && ItemStack.areItemStackTagsEqual(validBlock, other) && validBlock.getItemDamage() == other.getItemDamage());
    }

    public static boolean isTrowel(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (Mods.GT.isLoaded() && stack.getItem() instanceof MetaGeneratedTool metaGeneratedTool) {
            return metaGeneratedTool.getToolStats(stack) instanceof ToolTrowel;
        }
        return false;
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
            candidates.add(item);
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
