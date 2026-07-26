package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomStepper extends StepStrategy
{
    Random rand = new Random();

    public RandomStepper(TransportType transporting) {
        super(transporting);
    }

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent)
    {
        Block block = world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z);
        if (block instanceof BlockTransferBase transferBlock)
        {
            int meta = world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z);
            int validDirs = transferBlock.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDirection, meta, walkingComponent);
            List<ForgeDirection> dirList = new ArrayList<>();

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if ((validDirs & (1 << dir.ordinal())) != 0)
                {
                    if (world.getBlock(walkerPos.x + dir.offsetX,
                        walkerPos.y + dir.offsetY,
                        walkerPos.z + dir.offsetZ) instanceof BlockTransferBase)
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
