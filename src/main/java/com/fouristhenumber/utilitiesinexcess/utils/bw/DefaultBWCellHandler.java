package com.fouristhenumber.utilitiesinexcess.utils.bw;

import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.hitX;
import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.hitY;
import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.hitZ;
import static com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil.targetPos;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.config.items.BuildersWandsConfig;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

/**
 * An ordinary block: one position, one ItemStack, placed the way a player would place it. Also the fallback for
 * every cell no other handler claims, so plain blocks need no handler at all.
 * <p>
 * Extend it and override only the one that describes your block:
 * <ul>
 * <li>Which cells are even yours? {@link #handles} claims them, otherwise your handler won't be called at all.
 *
 * <li>Do you need custom selection spreading rules?
 * By default, spreading works by comparing itemStacks acquired by {@link #pickBlockAt} from clickedBlock and a
 * current block.
 * If block does not have one simple itemStack to return, e.g. for multipart cases, this will fail.
 * Override {@link #sameKindAsClicked} and compare them your way.
 *
 * <li>Does your block hand back a wrong {@code getPickBlock()}?
 * One that the inventory never contains, a dropped damage value or a leftover NBT tag.
 * Override {@link #pickBlockAt} and build the stack your block drops.
 *
 * <li>Does a cell cost more than its block, perhaps a cladding, a dye, a filter inside? Declare it in
 * {@link #extraCosts}
 * and the wand reserves it together with the block, or skips the cell.
 *
 * <li>Does the new block come out blank, missing a rotation, a color, a cover?
 * That state lives in the tile entity, and the wand hands the new block nothing but an ItemStack, which leaves it
 * behind. Override {@link #build}, call {@code super.build} first, then copy the tile entity state from the source
 * cell onto the new block, only when {@code place} is true.
 * </ul>
 * <p>
 * Is your cell not one block with one item at all, the way a bag of multiparts is not? Then implement
 * {@link IBWCellHandler} directly instead.
 */
public class DefaultBWCellHandler implements IBWCellHandler {

    @Override
    public boolean handles(BWContext ctx, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean build(BWContext ctx, MovingObjectPosition mop, boolean place) {
        World world = ctx.world;

        Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
        // ItemBlock places into a replaceable cell (air, grass, snow, fluid) instead of in front of it
        if (block.isReplaceable(world, mop.blockX, mop.blockY, mop.blockZ)) return false;

        ItemStack sourceBlockStack = pickBlockAt(ctx, mop);
        if (!(ctx.spreadsOverAnything() || sameKindAsClicked(ctx, mop, sourceBlockStack))) return false;

        ItemStack toPlace = ctx.picker.pickBlockFor(mop, sourceBlockStack, extraCosts(ctx, mop));
        if (toPlace == null) return false;
        if (!place) return true;

        if (!BWMode.tryDamageTrowel(ctx.player, BuildersWandsConfig.INSTANCE.damageTrowelWithBuildersWand))
            return false;

        ItemStack itemCopy = toPlace.copy();
        itemCopy.stackSize = 1;

        BlockPos to = targetPos(mop);
        ForgeDirection side = ForgeDirection.getOrientation(mop.sideHit);
        // extrude mode aims at the empty cell, clicking the source would merge slabs into a double slab
        boolean atTarget = ctx.isExtrudeMode();

        // uses ItemBlock to place the block with all the checks, sets stackSize to 0 on success
        itemCopy.tryPlaceItemIntoWorld(
            ctx.player,
            world,
            atTarget ? to.x : mop.blockX,
            atTarget ? to.y : mop.blockY,
            atTarget ? to.z : mop.blockZ,
            mop.sideHit,
            atTarget ? hitX(mop) - side.offsetX : hitX(mop),
            atTarget ? hitY(mop) - side.offsetY : hitY(mop),
            atTarget ? hitZ(mop) - side.offsetZ : hitZ(mop));
        if (itemCopy.stackSize != 0) return false;

        ctx.spend(toPlace);

        // copy the rotation or other metadata pieces that do not transfer through the itemStack
        if (ctx.isExtrudeMode()) {
            world.setBlockMetadataWithNotify(
                to.x,
                to.y,
                to.z,
                world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ),
                3);
        }
        return true;
    }

    /** What this cell costs besides the block itself */
    protected List<ItemStack> extraCosts(BWContext ctx, MovingObjectPosition mop) {
        return Collections.emptyList();
    }

    /**
     * The stack that stands for this cell: what the wand looks for in the inventory and what it places.
     * Override when the pick stack is not the one this block drops.
     */
    @Override
    @Nullable
    public ItemStack pickBlockAt(BWContext ctx, MovingObjectPosition mop) {
        return ctx.world.getBlock(mop.blockX, mop.blockY, mop.blockZ)
            .getPickBlock(mop, ctx.world, mop.blockX, mop.blockY, mop.blockZ, ctx.player);
    }

    /**
     * Whether this cell is the same kind as the one the player clicked. Override when a pick stack cannot tell.
     * Used to determine spreading to other blocks
     */
    protected boolean sameKindAsClicked(BWContext ctx, MovingObjectPosition mop, @Nullable ItemStack source) {
        return ItemUtil.areStacksEqual(ctx.clickedBlockStack, source);
    }

}
