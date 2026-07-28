package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


// Energy Steppers are unique in how they step. They are also unaffected by
// upgrades to step strategies
public class EnergyStepper extends StepStrategy
{
    LongArrayFIFOQueue stepQueue = new LongArrayFIFOQueue();
    LongOpenHashSet visitedLocations = new LongOpenHashSet();

    private final BlockPos mutablePos = new BlockPos();

    public EnergyStepper(TransportType type)
    {
        super(type);
    }

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        Block block = world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z);
        if (block instanceof BlockTransferBase transferBlock)
        {
            int meta = world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z);
            int validDirs = transferBlock.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDirection, meta, walkingComponent);

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if ((validDirs & (1 << dir.ordinal())) != 0)
                {
                    int nx = walkerPos.x;
                    int ny = walkerPos.y;
                    int nz = walkerPos.z;

                    if (world.getBlock(nx, ny, nz) instanceof BlockTransferBase)
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
