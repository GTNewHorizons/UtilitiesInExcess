package com.fouristhenumber.utilitiesinexcess.compat.architecturecraft;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWContext;
import com.fouristhenumber.utilitiesinexcess.utils.bw.DefaultBWCellHandler;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import gcewing.architecture.ArchitectureCraft;
import gcewing.architecture.common.tile.TileShape;

/**
 * Special Copy handler for ArchitectureCraft shapes.
 */
public final class ArchitectureCraftBWCellHandler extends DefaultBWCellHandler {

    @Override
    public boolean handles(BWContext ctx, int x, int y, int z) {
        return ctx.isExtrudeMode() && shapeAt(ctx.world, x, y, z) != null;
    }

    @Override
    public boolean build(BWContext ctx, MovingObjectPosition mop, boolean place) {
        if (!super.build(ctx, mop, place)) return false;
        if (!place) return true;

        BlockPos from = MovingObjectPositionUtil.blockPos(mop);
        copyShapeState(
            ctx,
            from,
            MovingObjectPositionUtil.targetPos(mop),
            claddingOf(shapeAt(ctx.world, from.x, from.y, from.z)));
        return true;
    }

    /** A clad shape costs its cladding too, so a copy nobody can pay for is never started. */
    @Override
    protected List<ItemStack> extraCosts(BWContext ctx, MovingObjectPosition mop) {
        BlockPos from = MovingObjectPositionUtil.blockPos(mop);
        ItemStack cladding = claddingOf(shapeAt(ctx.world, from.x, from.y, from.z));

        return cladding == null ? Collections.emptyList() : Collections.singletonList(cladding);
    }

    /** Copies the TileShape state the item cannot carry: side, turn, offset, connections and cladding. */
    private static void copyShapeState(BWContext ctx, BlockPos from, BlockPos to, @Nullable ItemStack cladding) {
        TileShape source = shapeAt(ctx.world, from.x, from.y, from.z);
        TileShape target = shapeAt(ctx.world, to.x, to.y, to.z);
        if (source == null || target == null) return;

        target.setSide(source.side);
        target.setTurn(source.turn);
        target.setOffsetX(source.getOffsetX());
        target.disabledConnections = source.disabledConnections;

        if (cladding != null) {
            // applySecondaryMaterial decrements what it is given, so it gets a copy and the budget pays
            target.applySecondaryMaterial(cladding.copy(), ctx.player);
            ctx.spend(cladding);
        }

        // markDirty plus a block update, otherwise the change is server side only until a relog
        target.markChanged();
    }

    /** The cladding stack this shape wears, null when it has none. */
    @Nullable
    private static ItemStack claddingOf(@Nullable TileShape shape) {
        if (shape == null || shape.secondaryBlockState == null) return null;

        // built by AC itself, so it matches whatever the source is clad with, NBT included
        return ArchitectureCraft.content.itemCladding.newStack(shape.secondaryBlockState, 1);
    }

    private static TileShape shapeAt(World world, int x, int y, int z) {
        return TileShape.get(world, new gcewing.architecture.compat.BlockPos(x, y, z));
    }
}
