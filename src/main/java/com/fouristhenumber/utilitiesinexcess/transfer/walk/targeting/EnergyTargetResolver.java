package com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting;

import cofh.api.energy.IEnergyConnection;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class EnergyTargetResolver implements TargetResolver<IEnergyConnection> {

    public EnergyTargetResolver()
    {}

    @Override
    public List<Target<IEnergyConnection>> getValidTargets(World world, BlockPos walkerPos, IWalkingComponent<?> walking, ForgeDirection fromDir)
    {
        List<Target<IEnergyConnection>> validTargets = new ArrayList<>();
        IConnectable connectable = IConnectable.getConnectable(world, walkerPos.x, walkerPos.y, walkerPos.z);
        if (connectable != null)
        {
            int validOuputDirs = connectable.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDir, walking);

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
                        instanceof IEnergyConnection target)
                    {
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
