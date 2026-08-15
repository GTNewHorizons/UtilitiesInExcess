package com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
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

        IConnectable connectable = IConnectable.getConnectable(world, walkerPos.x, walkerPos.y, walkerPos.z);
        if (connectable != null)
        {
            int validOutputDirs = connectable.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDir, walking);
            for (ForgeDirection searchDir : ForgeDirection.VALID_DIRECTIONS)
            {
                if ((validOutputDirs & (1 << searchDir.ordinal())) == 0)
                {
                    continue;
                }

                if (searchDir != fromDir) {
                    if (world.getTileEntity(
                        walkerPos.x + searchDir.offsetX,
                        walkerPos.y + searchDir.offsetY,
                        walkerPos.z + searchDir.offsetZ)
                        instanceof IFluidHandler target) {
                        validTargets.add(new Target<>(target, searchDir.getOpposite().ordinal(),
                            walkerPos.x + searchDir.offsetX,
                            walkerPos.y + searchDir.offsetY,
                            walkerPos.z + searchDir.offsetZ));
                    }
                }
            }
        }
        return validTargets;
    }

}
