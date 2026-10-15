package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;

import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class BFSStepper extends StepStrategy {

    private final LongArrayFIFOQueue stepQueue = new LongArrayFIFOQueue();
    private final ByteArrayFIFOQueue directionQueue = new ByteArrayFIFOQueue();
    private final LongOpenHashSet visitedLocations = new LongOpenHashSet();

    private final BlockPos mutablePos = new BlockPos();

    public BFSStepper() {
        super();
    }

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent) {
        List<IConnectable> connectables = IConnectable.getConnectables(world, walkerPos.x, walkerPos.y, walkerPos.z);
        if (!connectables.isEmpty()) {
            int validDirs = IConnectable.validWalkDirections(
                connectables,
                world,
                walkerPos.x,
                walkerPos.y,
                walkerPos.z,
                fromDirection,
                walkingComponent);

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if ((validDirs & (1 << dir.ordinal())) != 0) {
                    int nx = walkerPos.x + dir.offsetX;
                    int ny = walkerPos.y + dir.offsetY;
                    int nz = walkerPos.z + dir.offsetZ;

                    List<IConnectable> candidateConnectables = IConnectable.getConnectables(world, nx, ny, nz);
                    if (!candidateConnectables.isEmpty() && IConnectable
                        .canConnectInDirection(candidateConnectables, world, nx, ny, nz, dir.getOpposite())) {
                        long packedCoord = CoordinatePacker.pack(nx, ny, nz);
                        if (visitedLocations.add(packedCoord)) {
                            stepQueue.enqueue(packedCoord);
                            directionQueue.enqueue(
                                (byte) dir.getOpposite()
                                    .ordinal());
                        }
                    }
                }
            }
            if (stepQueue.isEmpty()) {
                return reset(walkerPos, walkingComponent);
            } else {
                fromDirection = ForgeDirection.VALID_DIRECTIONS[directionQueue.dequeueByte()];
                return mutablePos.set(stepQueue.dequeueLong());
            }
        }
        return reset(walkerPos, walkingComponent);
    }

    @Override
    public BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent) {
        stepQueue.clear();
        directionQueue.clear();
        visitedLocations.clear();
        visitedLocations
            .add(CoordinatePacker.pack(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ()));
        return walkerPos.set(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ());
    }
}
