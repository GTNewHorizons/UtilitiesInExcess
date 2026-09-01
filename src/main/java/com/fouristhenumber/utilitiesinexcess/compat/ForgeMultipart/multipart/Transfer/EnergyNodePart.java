package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNodeEnergy;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.NodeCollision;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase.getFacingOrdinal;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic.isValidConnectable;

public class EnergyNodePart extends BaseNodePart<EnergyTransferNodeLogic, Integer>
{
    public EnergyNodePart(int meta) {
        super(meta);
    }

    public EnergyNodePart(MCDataInput packet)
    {
        super(packet.readInt());
        getLogic().readDesc(packet);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.TRANSFER_NODE_ENERGY.get();
    }

    @Override
    protected EnergyTransferNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new EnergyTransferNodeLogic(this);
        }
        return logic;
    }

    @Override
    public String getType() {
        return ConversionRegistry.EnergyTransferNode.getName();
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    // Needs to look at blocks in the way
    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        return !doPartsOccludeDirection(direction);
    }

    @Override
    public Cuboid6 getBounds() {
        return new Cuboid6(BlockTransferNodeEnergy.basicBounds);
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(new Cuboid6(BlockTransferNodeEnergy.basicBounds));
    }

    @Override
    public ForgeDirection getFacing() {
        return ForgeDirection.UNKNOWN;
    }

    @Override
    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return !doPartsOccludeDirection(dir) && isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
    }

    @Override
    public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        if (fromDirection != ForgeDirection.UNKNOWN)
        {
            mask &= ~(1 << fromDirection.ordinal());
        }
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            int bit = 1 << dir.ordinal();

            if ((mask & bit) == 0)
            {
                continue;
            }

            if (doPartsOccludeDirection(dir))
            {
                mask &= ~bit;
            }
        }
        return mask;
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        return Collections.singleton(new IndexedCuboid6(0, this.getBounds()));
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes()
    {
        List<Cuboid6> cuboid6s = new ArrayList<>();
        for (AxisAlignedBB aabb : BlockTransferNodeEnergy.getBlockCenteredCollisionCandidates(world(), x(), y(), z(), meta))
        {
            cuboid6s.add(new Cuboid6(aabb));
        }
        return cuboid6s;
    }

}
