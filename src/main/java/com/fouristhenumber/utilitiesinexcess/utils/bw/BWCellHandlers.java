package com.fouristhenumber.utilitiesinexcess.utils.bw;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;

import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

/**
 * The Builder's Wand's extension point.
 * Add your own custom place handler by implementing IBWCellHandler and registering it here.
 *
 * Useful particularly for extrude mode, when naively copying just the metadata of a block is not enough.
 * Also useful for any blocks for which the vanilla Block to ItemStack method getPickBlock() is tricky
 * (like ForgeMicroblocks)
 */
public final class BWCellHandlers {

    private static final List<IBWCellHandler> handlers = new ArrayList<>();
    private static final IBWCellHandler defaultHandler = new DefaultBWCellHandler();

    private BWCellHandlers() {}

    public static void register(IBWCellHandler handler) {
        handlers.add(handler);
    }

    /**
     * Hands one cell of the fill to whichever handler claims it
     *
     * @param ctx   The context of the wand
     * @param mop   looking at the block on which to build
     * @param place false to only reserve and report, true to actually build and charge the player
     * @return true if this cell is part of the fill
     */
    public static boolean build(BWContext ctx, MovingObjectPosition mop, boolean place) {
        if (place && spawnProtected(ctx, mop)) return false;
        return find(ctx, mop.blockX, mop.blockY, mop.blockZ).build(ctx, mop, place);
    }

    /** Vanilla checks spawn protection only for the block the player clicked, not for the rest of the fill. */
    private static boolean spawnProtected(BWContext ctx, MovingObjectPosition mop) {
        BlockPos to = MovingObjectPositionUtil.targetPos(mop);
        return MinecraftServer.getServer()
            .isBlockProtected(ctx.world, to.x, to.y, to.z, ctx.player);
    }

    public static IBWCellHandler find(BWContext ctx, int x, int y, int z) {
        for (IBWCellHandler handler : handlers) {
            if (handler.handles(ctx, x, y, z)) return handler;
        }
        return defaultHandler;
    }
}
