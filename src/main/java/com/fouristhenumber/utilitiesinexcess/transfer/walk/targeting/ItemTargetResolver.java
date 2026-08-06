package com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.utils.InventoryUtils.getInventory;

public class ItemTargetResolver implements TargetResolver<IInventory> {

    public ItemTargetResolver()
    {
    }

    @Override
    public List<Target<IInventory>> getValidTargets(World world, BlockPos walkerPos, IWalkingComponent<?> walking, ForgeDirection fromDir)
    {
        List<Target<IInventory>> validTargets = new ArrayList<>();

        Block block = world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z);
        if (block instanceof BlockTransferBase transferBlock)
        {
            int meta = world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z);

            int validOutputDirs = transferBlock.validWalkDirections(world, walkerPos.x, walkerPos.y, walkerPos.z, fromDir, meta, walking);

            for (ForgeDirection searchDir : ForgeDirection.VALID_DIRECTIONS)
            {
                if ((validOutputDirs & (1 << searchDir.ordinal())) == 0)
                {
                    continue;
                }

                IInventory inv = getInventory(world,
                    walkerPos.x + searchDir.offsetX,
                    walkerPos.y + searchDir.offsetY,
                    walkerPos.z + searchDir.offsetZ);
                if (inv != null)
                {
                    validTargets.add(new Target<>(inv, searchDir.getOpposite().ordinal(),
                        walkerPos.x + searchDir.offsetX,
                        walkerPos.y + searchDir.offsetY,
                        walkerPos.z + searchDir.offsetZ));
                }
            }
        }
        return validTargets;
    }
}
