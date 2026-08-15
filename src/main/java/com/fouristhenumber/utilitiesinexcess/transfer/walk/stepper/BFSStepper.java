package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BFSStepper extends StepStrategy
{
    LongArrayFIFOQueue stepQueue = new LongArrayFIFOQueue();
    LongOpenHashSet visitedLocations = new LongOpenHashSet();

    private final BlockPos mutablePos = new BlockPos();

    public BFSStepper()
    {
        super();
    }

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        IConnectable connectable = IConnectable.getConnectable(world, walkerPos.x, walkerPos.y, walkerPos.z);
        if (connectable != null)
        {
            int validDirs = connectable.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDirection, walkingComponent);

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if ((validDirs & (1 << dir.ordinal())) != 0)
                {
                    int nx = walkerPos.x + dir.offsetX;
                    int ny = walkerPos.y + dir.offsetY;
                    int nz = walkerPos.z + dir.offsetZ;

                    if (IConnectable.getConnectable(world, nx, ny, nz) != null)
                    {
                        long packedCoord = CoordinatePacker.pack(nx, ny, nz);
                        if (visitedLocations.add(packedCoord))
                        {
                            stepQueue.enqueue(packedCoord);
                        }
                    }
                }
            }
            if (stepQueue.isEmpty())
            {
                return reset(walkerPos, walkingComponent);
            }
            else
            {
                return mutablePos.set(stepQueue.dequeueLong());
            }
        }
        return reset(walkerPos, walkingComponent);
    }

    @Override
    public BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        stepQueue.clear();
        visitedLocations.clear();
        visitedLocations.add(CoordinatePacker.pack(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ()));
        return walkerPos.set(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ());
    }
}
