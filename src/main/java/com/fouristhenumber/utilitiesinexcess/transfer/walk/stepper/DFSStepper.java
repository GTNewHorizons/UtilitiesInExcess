package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayDeque;

public class DFSStepper extends StepStrategy
{
    private static class Frame
    {
        final int x, y, z;
        int remainingDirs; // bitmask of directions not yet attempted from this node

        Frame(int x, int y, int z, int remainingDirs)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.remainingDirs = remainingDirs;
        }
    }

    private final ArrayDeque<Frame> stack = new ArrayDeque<>();
    private final LongOpenHashSet visitedLocations = new LongOpenHashSet();
    private final BlockPos mutablePos = new BlockPos();

    public DFSStepper() {}

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        Block block = world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z);
        if (!(block instanceof BlockTransferBase transferBlock))
        {
            return reset(walkerPos, walkingComponent);
        }

        int meta = world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z);
        int validDirs = transferBlock.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDirection, meta, walkingComponent);

        int remaining = filterToUnvisited(world, walkerPos.x, walkerPos.y, walkerPos.z, validDirs);

        if (remaining != 0)
        {
            ForgeDirection chosen = firstDirection(remaining);
            int leftover = remaining & ~(1 << chosen.ordinal());

            stack.push(new Frame(walkerPos.x, walkerPos.y, walkerPos.z, leftover));

            int nx = walkerPos.x + chosen.offsetX;
            int ny = walkerPos.y + chosen.offsetY;
            int nz = walkerPos.z + chosen.offsetZ;

            visitedLocations.add(CoordinatePacker.pack(nx, ny, nz));
            return mutablePos.set(nx, ny, nz);
        }

        while (!stack.isEmpty())
        {
            Frame top = stack.peek();

            if (top.remainingDirs == 0)
            {
                stack.pop();
                continue;
            }

            ForgeDirection chosen = firstDirection(top.remainingDirs);
            top.remainingDirs &= ~(1 << chosen.ordinal());

            int nx = top.x + chosen.offsetX;
            int ny = top.y + chosen.offsetY;
            int nz = top.z + chosen.offsetZ;
            long packed = CoordinatePacker.pack(nx, ny, nz);

            if (world.getBlock(nx, ny, nz) instanceof BlockTransferBase && visitedLocations.add(packed))
            {
                return mutablePos.set(nx, ny, nz);
            }
        }

        return reset(walkerPos, walkingComponent);
    }

    private int filterToUnvisited(World world, int x, int y, int z, int validDirs)
    {
        int result = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if ((validDirs & (1 << dir.ordinal())) == 0)
            {
                continue;
            }

            int nx = x + dir.offsetX;
            int ny = y + dir.offsetY;
            int nz = z + dir.offsetZ;

            if (world.getBlock(nx, ny, nz) instanceof BlockTransferBase
                && !visitedLocations.contains(CoordinatePacker.pack(nx, ny, nz)))
            {
                result |= (1 << dir.ordinal());
            }
        }
        return result;
    }

    private ForgeDirection firstDirection(int mask)
    {
        return ForgeDirection.VALID_DIRECTIONS[Integer.numberOfTrailingZeros(mask)];
    }

    @Override
    public BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        stack.clear();
        visitedLocations.clear();
        visitedLocations.add(CoordinatePacker.pack(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ()));
        return walkerPos.set(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ());
    }
}
