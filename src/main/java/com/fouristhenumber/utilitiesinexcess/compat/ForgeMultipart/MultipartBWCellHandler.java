package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWContext;
import com.fouristhenumber.utilitiesinexcess.utils.bw.DefaultBWCellHandler;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.BlockMultipart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import scala.Tuple2;

/**
 * A ForgeMultipart cell is a bag of parts rather than one block with one item.
 * <ul>
 * <li>extrude mode: the whole cell, all the parts or none
 * <li>trowel: the multipart is just a surface to paint over
 * <li>offhand block: placed in front of every cell holding the part the player pointed at
 * <li>clicked block: that part itself spreads over every cell holding it
 * </ul>
 */
public final class MultipartBWCellHandler extends DefaultBWCellHandler {

    @Override
    public boolean handles(BWContext ctx, int x, int y, int z) {
        TileMultipart tile = BlockMultipart.getTile(ctx.world, x, y, z);
        return tile != null && !tile.jPartList()
            .isEmpty();
    }

    @Override
    public boolean build(BWContext ctx, MovingObjectPosition mop, boolean place) {
        return switch (ctx.mode) {
            case EXTRUDE -> copyWholeCell(ctx, mop, place);
            case CLICKED_BLOCK -> extendPointedPart(ctx, mop, place);
            case TROWEL, OFFHAND_BLOCK -> super.build(ctx, mop, place);
        };
    }

    /** A multipart cell is the same kind if it holds the part the player pointed at. */
    @Override
    protected boolean sameKindAsClicked(BWContext ctx, MovingObjectPosition mop, @Nullable ItemStack source) {
        TMultiPart pointed = pointedPart(ctx);
        return pointed != null && holdsSame(ctx.world, mop, pointed);
    }

    /** All the parts or none. */
    private static boolean copyWholeCell(BWContext ctx, MovingObjectPosition mop, boolean place) {
        World world = ctx.world;
        BlockPos from = MovingObjectPositionUtil.blockPos(mop);
        BlockPos to = MovingObjectPositionUtil.targetPos(mop);

        List<ItemStack> costs = partCosts(world, from);
        if (costs.isEmpty()) return false;

        List<TMultiPart> clones = fittingClones(world, from, to);
        if (clones == null || !ctx.budget.tryReserve(costs)) return false;
        if (!place) return true;

        if (!tryPlaceParts(ctx, mop, clones)) return false;
        costs.forEach(ctx::spend);
        return true;
    }

    private static boolean extendPointedPart(BWContext ctx, MovingObjectPosition mop, boolean place) {
        TMultiPart pointed = pointedPart(ctx);
        if (pointed == null) return false;

        // only spread onto blocks containing the looked at part
        if (!holdsSame(ctx.world, mop, pointed)) return false;

        // canPlacePart also allows fitting the part into a cell that already holds other parts
        BlockPos to = MovingObjectPositionUtil.targetPos(mop);
        BlockCoord target = new BlockCoord(to.x, to.y, to.z);
        TMultiPart clone = clone(pointed);
        if (clone == null || !TileMultipart.canPlacePart(ctx.world, target, clone)) return false;

        List<ItemStack> costs = drops(pointed);
        if (costs.isEmpty() || !ctx.budget.tryReserve(costs)) return false;
        if (!place) return true;

        if (!tryPlaceParts(ctx, mop, List.of(clone))) return false;
        costs.forEach(ctx::spend);
        return true;
    }

    /** The part the player pointed at, e.g. a pane */
    private static TMultiPart pointedPart(BWContext ctx) {
        MovingObjectPosition clicked = ctx.originalMop;
        if (!(clicked instanceof ExtendedMOP extended) || !(extended.data instanceof Tuple2<?, ?>data)) return null;
        if (!(data._1() instanceof Integer index)) return null;

        TileMultipart tile = BlockMultipart.getTile(ctx.world, clicked.blockX, clicked.blockY, clicked.blockZ);
        if (tile == null || index < 0
            || index >= tile.jPartList()
                .size())
            return null;
        return tile.jPartList()
            .get(index);
    }

    /** Whether this cell holds a part matching the pointed one: same kind, same slot and size, same material. */
    private static boolean holdsSame(World world, MovingObjectPosition mop, TMultiPart pointed) {
        TileMultipart tile = BlockMultipart.getTile(world, mop.blockX, mop.blockY, mop.blockZ);
        if (tile == null) return false;

        NBTTagCompound wanted = saved(pointed);
        for (TMultiPart part : tile.jPartList()) {
            if (part.getType()
                .equals(pointed.getType()) && saved(part).equals(wanted)) return true;
        }
        return false;
    }

    /** Every item the parts at this position would cost to rebuild. */
    private static List<ItemStack> partCosts(World world, BlockPos pos) {
        TileMultipart tile = BlockMultipart.getTile(world, pos.x, pos.y, pos.z);
        if (tile == null) return new ArrayList<>();

        List<ItemStack> costs = new ArrayList<>();
        for (TMultiPart part : tile.jPartList()) {
            costs.addAll(drops(part));
        }
        return costs;
    }

    private static List<ItemStack> drops(TMultiPart part) {
        List<ItemStack> costs = new ArrayList<>();
        for (ItemStack drop : part.getDrops()) {
            if (drop != null) costs.add(drop);
        }
        return costs;
    }

    /** @return clones of every part at from, or null when they do not all fit into the empty cell at to */
    private static List<TMultiPart> fittingClones(World world, BlockPos from, BlockPos to) {
        // only into empty space, canPlacePart alone would merge into an existing multipart
        if (!world.getBlock(to.x, to.y, to.z)
            .isReplaceable(world, to.x, to.y, to.z)) return null;

        TileMultipart source = BlockMultipart.getTile(world, from.x, from.y, from.z);
        if (source == null || source.jPartList()
            .isEmpty()) return null;

        BlockCoord target = new BlockCoord(to.x, to.y, to.z);
        List<TMultiPart> clones = new ArrayList<>();
        for (TMultiPart part : source.jPartList()) {
            TMultiPart clone = clone(part);
            if (clone == null || !TileMultipart.canPlacePart(world, target, clone)) return null;
            clones.add(clone);
        }
        return clones;
    }

    /**
     * Adds the parts to the cell, checks if the player is allowed to place them, and rolls back if not.
     * (So we don't allow placing into forbidden territories)
     * (Necessary, since FMP does not issue onPlaceEvent when a cell is just modified)
     *
     * @return true on success
     */
    private static boolean tryPlaceParts(BWContext ctx, MovingObjectPosition mop, List<TMultiPart> parts) {
        BlockPos to = MovingObjectPositionUtil.targetPos(mop);
        BlockSnapshot before = BlockSnapshot.getBlockSnapshot(ctx.world, to.x, to.y, to.z);
        BlockCoord target = new BlockCoord(to.x, to.y, to.z);

        // place
        for (TMultiPart part : parts) TileMultipart.addPart(ctx.world, target, part);

        // check if we are allowed to place it
        PlaceEvent event = ForgeEventFactory
            .onPlayerBlockPlace(ctx.player, before, ForgeDirection.getOrientation(mop.sideHit));
        if (!event.isCanceled()) return true;

        // rollback if necessary
        // (cannot use silently snapshot.restore(), since the TileMultipart.addPart already queued a message to clients)
        for (TMultiPart part : parts) {
            TileMultipart tile = BlockMultipart.getTile(ctx.world, to.x, to.y, to.z);
            if (tile != null) tile.remPart(part);
        }
        return false;
    }

    private static NBTTagCompound saved(TMultiPart part) {
        NBTTagCompound nbt = new NBTTagCompound();
        part.save(nbt);
        return nbt;
    }

    /**
     * Round trip through NBT; loadPart only constructs, hence the load after it.
     */
    private static TMultiPart clone(TMultiPart part) {
        NBTTagCompound nbt = saved(part);
        TMultiPart clone = MultiPartRegistry.loadPart(part.getType(), nbt);
        if (clone != null) clone.load(nbt);
        return clone;
    }
}
