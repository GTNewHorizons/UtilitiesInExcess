package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.BaseNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public abstract class BaseNodePart <T extends BaseNodeLogic<?, V>, V> extends PartNetworkComponentBase<T> implements IWalkingComponent<V>
{
    public BaseNodePart(int meta) {
        super(meta);
    }

    @Override
    public void update()
    {
        getLogic().updateEntity();
    }

    @Override
    protected abstract T getLogic();

    @Override
    public Cuboid6 getBounds() {
        return new Cuboid6(0, 0, 0, 0, 0, 0);
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
}
