// Copied from U25Core
package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class DirectionUtil {

    public static final ForgeDirection[] HORIZONTAL_DIRECTIONS = new ForgeDirection[] { ForgeDirection.NORTH,
        ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };

    public static ForgeDirection turnRight90(ForgeDirection dir) {
        return switch (dir) {
            case EAST -> ForgeDirection.SOUTH;
            case NORTH -> ForgeDirection.EAST;
            case SOUTH -> ForgeDirection.WEST;
            case WEST -> ForgeDirection.NORTH;
            default -> dir;
        };
    }

    public static ForgeDirection turnLeft90(ForgeDirection dir) {
        return switch (dir) {
            case EAST -> ForgeDirection.NORTH;
            case NORTH -> ForgeDirection.WEST;
            case SOUTH -> ForgeDirection.EAST;
            case WEST -> ForgeDirection.SOUTH;
            default -> dir;
        };
    }

    public static ForgeDirection turn180(ForgeDirection dir) {
        return switch (dir) {
            case EAST -> ForgeDirection.WEST;
            case NORTH -> ForgeDirection.SOUTH;
            case SOUTH -> ForgeDirection.NORTH;
            case WEST -> ForgeDirection.EAST;
            default -> dir;
        };
    }

    public static ForgeDirection turnRight270(ForgeDirection dir) {
        return turnLeft90(dir);
    }

    public static ForgeDirection turnLeft270(ForgeDirection dir) {
        return turnRight90(dir);
    }

    /**
     * Offsets coordinates to the right based on the facing direction
     *
     * @param pos      Current position
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset to the right
     */
    public static BlockPos offsetByRight(BlockPos pos, ForgeDirection facing, int amount, int vertical) {
        ForgeDirection rightDir = turnRight90(facing);
        return new BlockPos(pos.x + rightDir.offsetX * amount, pos.y + vertical, pos.z + rightDir.offsetZ * amount);
    }

    /**
     * Offsets coordinates to the right based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset to the right
     */
    public static BlockPos offsetByRight(int x, int y, int z, ForgeDirection facing, int amount, int vertical) {
        return offsetByRight(new BlockPos(x, y, z), facing, amount, vertical);
    }

    /**
     * Offsets coordinates to the right based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset to the right
     */
    public static BlockPos offsetByRight(int x, int y, int z, ForgeDirection facing, int amount, int vertical,
        boolean relative) {
        BlockPos pos = offsetByRight(new BlockPos(x, y, z), facing, amount, vertical);
        if (relative) {
            pos.x = x - pos.x;
            pos.y = y - pos.y;
            pos.z = z - pos.z;
        }
        return pos;
    }

    /**
     * Offsets coordinates to the left based on the facing direction
     *
     * @param pos      Current position
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset to the left
     */
    public static BlockPos offsetByLeft(BlockPos pos, ForgeDirection facing, int amount, int vertical) {
        ForgeDirection leftDir = turnLeft90(facing);
        return new BlockPos(pos.x + leftDir.offsetX * amount, pos.y + vertical, pos.z + leftDir.offsetZ * amount);
    }

    /**
     * Offsets coordinates to the left based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset to the left
     */
    public static BlockPos offsetByLeft(int x, int y, int z, ForgeDirection facing, int amount, int vertical) {
        return offsetByLeft(new BlockPos(x, y, z), facing, amount, vertical);
    }

    /**
     * Offsets coordinates to the left based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset to the left
     */
    public static BlockPos offsetByLeft(int x, int y, int z, ForgeDirection facing, int amount, int vertical,
        boolean relative) {
        BlockPos pos = offsetByLeft(new BlockPos(x, y, z), facing, amount, vertical);
        if (relative) {
            pos.x = x - pos.x;
            pos.y = y - pos.y;
            pos.z = z - pos.z;
        }
        return pos;
    }

    /**
     * Offsets coordinates backwards based on the facing direction
     *
     * @param pos      Current position
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset backwards
     */
    public static BlockPos offsetByBack(BlockPos pos, ForgeDirection facing, int amount, int vertical) {
        ForgeDirection backDir = facing.getOpposite();
        return new BlockPos(pos.x + backDir.offsetX * amount, pos.y + vertical, pos.z + backDir.offsetZ * amount);
    }

    /**
     * Offsets coordinates backwards based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset backwards
     */
    public static BlockPos offsetByBack(int x, int y, int z, ForgeDirection facing, int amount, int vertical) {
        return offsetByBack(new BlockPos(x, y, z), facing, amount, vertical);
    }

    /**
     * Offsets coordinates backwards based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset backwards
     */
    public static BlockPos offsetByBack(int x, int y, int z, ForgeDirection facing, int amount, int vertical,
        boolean relative) {
        BlockPos pos = offsetByBack(new BlockPos(x, y, z), facing, amount, vertical);
        if (relative) {
            pos.x = x - pos.x;
            pos.y = y - pos.y;
            pos.z = z - pos.z;
        }
        return pos;
    }

    /**
     * Offsets coordinates forward based on the facing direction
     *
     * @param pos      Current position
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset forward
     */
    public static BlockPos offsetByForward(BlockPos pos, ForgeDirection facing, int amount, int vertical) {
        return new BlockPos(pos.x + facing.offsetX * amount, pos.y + vertical, pos.z + facing.offsetZ * amount);
    }

    /**
     * Offsets coordinates forward based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset forward
     */
    public static BlockPos offsetByForward(int x, int y, int z, ForgeDirection facing, int amount, int vertical) {
        return offsetByForward(x, y, z, facing, amount, vertical, false);
    }

    /**
     * Offsets coordinates forward based on the facing direction
     *
     * @param x        Current X coordinate
     * @param y        Current Y coordinate
     * @param z        Current Z coordinate
     * @param facing   The direction being faced
     * @param amount   Number of blocks to offset
     * @param vertical Number of blocks to offset vertically (positive = up, negative = down)
     * @return New BlockPos offset forward
     */
    public static BlockPos offsetByForward(int x, int y, int z, ForgeDirection facing, int amount, int vertical,
        boolean relative) {
        BlockPos pos = offsetByForward(new BlockPos(x, y, z), facing, amount, vertical);
        if (relative) {
            pos.x = x - pos.x;
            pos.y = y - pos.y;
            pos.z = z - pos.z;
        }
        return pos;
    }
}
