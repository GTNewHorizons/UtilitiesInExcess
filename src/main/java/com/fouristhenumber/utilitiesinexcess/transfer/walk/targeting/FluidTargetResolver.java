package com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class FluidTargetResolver implements TargetResolver<IFluidHandler>
{

    public FluidTargetResolver()
    {}

    @Override
    public List<Target<IFluidHandler>> getValidTargets(World world, BlockPos walkerPos, IWalkingComponent<?> walking, ForgeDirection fromDir) {
        List<Target<IFluidHandler>> validTargets = new ArrayList<>();

        Block block = world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z);
        if (block instanceof BlockTransferBase networkBlock)
        {
            int meta = world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z);

            int validOuputDirs = networkBlock.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDir, meta, walking);

            for (ForgeDirection searchDir : ForgeDirection.VALID_DIRECTIONS)
            {
                if ((validOuputDirs & (1 << searchDir.ordinal())) == 0)
                {
                    continue;
                }

                if (searchDir != fromDir) {
                    if (world.getTileEntity(
                        walkerPos.x + searchDir.offsetX,
                        walkerPos.y + searchDir.offsetY,
                        walkerPos.z + searchDir.offsetZ)
                        instanceof IFluidHandler target) {
                        validTargets.add(new Target<>(target, searchDir.getOpposite().ordinal()));
                    }
                }
            }
        }
        return validTargets;
    }

}
