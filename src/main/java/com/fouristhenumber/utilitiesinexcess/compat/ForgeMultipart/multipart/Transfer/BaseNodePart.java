package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.BaseNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase.getFacingOrdinal;

public abstract class BaseNodePart <T extends BaseNodeLogic<?, V>, V> extends PartNetworkComponentBase implements IWalkingComponent<V>
{
    protected T logic;

    public BaseNodePart(int meta) {
        super(meta);
    }

    @Override
    public void update()
    {
        getLogic().updateEntity();
    }

    protected abstract T getLogic();

    @Override
    public Cuboid6 getBounds() {
        return new Cuboid6(BlockNodeBase.getBoundsAABB(meta, this.getConnectionMask(world(), x(), y(), z())));
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return new ArrayList<Cuboid6>();
    }

    @Override
    public abstract String getType();

    @Override
    public V getWalkingObject() {
        return logic.getWalkingObject();
    }

    @Override
    public ForgeDirection getFacing() {
        return BlockNodeBase.getFacing(meta);
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        if (tile() == null) {
            return false;
        }
        return !doPartsOccludeDirection(direction);
    }

    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        int facing = getFacingOrdinal(meta);
        if (facing < 6)
        {
            mask &= ~(1 << facing);
        }
        if (fromDirection != ForgeDirection.UNKNOWN)
        {
            mask &= ~(1 << fromDirection.ordinal());
        }
        return mask;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z) {
        return 0;
    }
}
