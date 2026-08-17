package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomStepper extends StepStrategy
{
    Random rand = new Random();

    public RandomStepper() {
        super();
    }

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        List<IConnectable> connectables = IConnectable.getConnectables(world, walkerPos.x, walkerPos.y, walkerPos.z);
        if (!connectables.isEmpty())
        {
            int validDirs = IConnectable.validWalkDirections(connectables, world, walkerPos.x, walkerPos.y, walkerPos.z, fromDirection, walkingComponent);
            List<ForgeDirection> dirList = new ArrayList<>();

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if ((validDirs & (1 << dir.ordinal())) != 0)
                {
                    int nx = walkerPos.x + dir.offsetX;
                    int ny = walkerPos.y + dir.offsetY;
                    int nz = walkerPos.z + dir.offsetZ;

                    List<IConnectable> candidateConnectables = IConnectable.getConnectables(world, nx, ny, nz);
                    if (!candidateConnectables.isEmpty() && IConnectable.canConnectInDirection(candidateConnectables, world, nx, ny, nz, dir.getOpposite()))
                    {
                        dirList.add(dir);
                    }
                }
            }

            if (!dirList.isEmpty())
            {
                ForgeDirection chosenDir = dirList.get(rand.nextInt(dirList.size()));
                fromDirection = chosenDir.getOpposite();
                return walkerPos.offset(chosenDir);
            }
        }
        return reset(walkerPos, walkingComponent);
    }

    @Override
    public BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent) {
        return walkerPos.set(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ());
    }
}
