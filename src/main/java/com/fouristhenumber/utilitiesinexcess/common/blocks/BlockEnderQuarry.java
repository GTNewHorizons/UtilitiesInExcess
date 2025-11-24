package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderQuarry;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class BlockEnderQuarry extends BlockContainer {

    public BlockEnderQuarry() {
        super(Material.iron);
        setBlockName("ender_quarry");
        setBlockTextureName("utilitiesinexcess:ender_quarry");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);

        int direction = (int) (((placer.rotationYaw + 45f) / 90f + 4f) % 4f);

        worldIn.setBlockMetadataWithNotify(x, y, z, direction, 2);

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderQuarry quarry) {
            quarry.facing = getFacing(direction);
            quarry.resetState();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderQuarry quarry) {
            if (quarry.state == TileEntityEnderQuarry.QuarryWorkState.STOPPED) {
                BlockPos inFront = offsetByForward(x, y, z, quarry.facing, 1, 0);
                BlockPos farFront = offsetByForward(inFront, quarry.facing, 64, 0);
                farFront = offsetByRight(farFront, quarry.facing, 64, 0);

                // for (int i = 1; i < 256; i++) {
                // worldIn.setBlock(inFront.x, i, inFront.z, Blocks.diamond_block);
                // worldIn.setBlock(farFront.x, i, farFront.z, Blocks.diamond_block);
                // }

                player.addChatComponentMessage(
                    new ChatComponentText(String.format("Set up work area from %s to %s", inFront, farFront)));

                quarry.setWorkArea(new TileEntityEnderQuarry.Area2d(inFront.x, inFront.z, farFront.x, farFront.z));
                quarry.state = TileEntityEnderQuarry.QuarryWorkState.RUNNING;
            }
            player.addChatComponentMessage(new ChatComponentText(quarry.getState()));
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (!worldIn.isRemote && te instanceof TileEntityEnderQuarry quarry) {
            quarry.scanSidesForTEs();
        }
    }

    public static ForgeDirection getFacing(int meta) {
        return switch (meta) {
            case 0 -> ForgeDirection.SOUTH;
            case 1 -> ForgeDirection.WEST;
            case 2 -> ForgeDirection.NORTH;
            case 3 -> ForgeDirection.EAST;
            default -> ForgeDirection.UNKNOWN;
        };
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnderQuarry();
    }

    // TODO: REMOVE
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
