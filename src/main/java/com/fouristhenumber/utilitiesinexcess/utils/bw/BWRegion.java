package com.fouristhenumber.utilitiesinexcess.utils.bw;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

/**
 * Works out which cells one Builder's Wand use covers.
 * <ul>
 * <li>flood fills outward from the clicked block, in the plane of the face that was clicked
 * <li>a cell joins only if its handler says it can be built and paid for
 * </ul>
 */
public final class BWRegion {

    private BWRegion() {}

    /**
     * @param maxCount The maximum amount of blocks it should return
     * @param mop      The position of the block that was clicked (with its clicked side)
     * @return source blocks on top of which new blocks can be placed,
     *         at most maxCount cells, possibly none
     */
    public static Set<BlockPos> findAdjacentBlocksToBuildOn(BWContext ctx, int maxCount, MovingObjectPosition mop,
        WandAxisLock axisMode) {
        Set<BlockPos> region = LinkedHashSet.newLinkedHashSet(maxCount);
        if (maxCount <= 0) {
            return region;
        }
        Set<BlockPos> visited = HashSet.newHashSet(maxCount);
        Queue<BlockPos> queue = new ArrayDeque<>();

        // copy the mop so we can translate it without affecting the original
        mop = MovingObjectPositionUtil.copy(mop);

        // Determine allowed offsets depending on the face that was clicked.
        int[][] allowedOffsets = switch (ForgeDirection.getOrientation(mop.sideHit)) {
            case UP, DOWN -> switch (axisMode) {
                    case FREE -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 1, 0, 1 },
                        { 1, 0, -1 }, { -1, 0, 1 }, { -1, 0, -1 } };
                    case HORIZONTAL -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 } };
                    case VERTICAL -> new int[][] { { 0, 0, 1 }, { 0, 0, -1 } };
                };
            case NORTH, SOUTH -> switch (axisMode) {
                    case FREE -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 1, 0 },
                        { 1, -1, 0 }, { -1, 1, 0 }, { -1, -1, 0 } };
                    case HORIZONTAL -> new int[][] { { 1, 0, 0 }, { -1, 0, 0 } };
                    case VERTICAL -> new int[][] { { 0, 1, 0 }, { 0, -1, 0 } };
                };
            case EAST, WEST -> switch (axisMode) {
                    case FREE -> new int[][] { { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 0, 1, 1 },
                        { 0, 1, -1 }, { 0, -1, 1 }, { 0, -1, -1 } };
                    case HORIZONTAL -> new int[][] { { 0, 0, 1 }, { 0, 0, -1 } };
                    case VERTICAL -> new int[][] { { 0, 1, 0 }, { 0, -1, 0 } };
                };
            default -> throw new RuntimeException("UE's BuilderWand's findAdjacentBlocks called with invalid side");
        };

        // start block
        BlockPos startPos = new BlockPos(mop.blockX, mop.blockY, mop.blockZ);
        queue.add(startPos);
        visited.add(startPos);

        // Flood-fill the contiguous region in the allowed plane.
        while (!queue.isEmpty() && region.size() < maxCount) {
            BlockPos current = queue.poll();
            MovingObjectPositionUtil.TranslateMovingObjectPositionToLocation(mop, current);
            if (!BWCellHandlers.build(ctx, mop, false)) continue;

            region.add(current);

            for (int[] off : allowedOffsets) {
                if (region.size() >= maxCount) break;
                BlockPos key = current.offset(off[0], off[1], off[2]);
                if (visited.add(key)) queue.add(key);
            }
        }
        return region;
    }

    public enum WandAxisLock {
        FREE,
        HORIZONTAL,
        VERTICAL
    }

}
