package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConnectablePart;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TransferNodePart<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends PartNetworkComponentBase<T>
{
    protected TransferNodePart(int side) {
        super(side);
    }

    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side) {
        return null;
    }

    @Override
    public void render(Vector3 position, int pass) {

    }

    @Override
    public Cuboid6 getBounds() {
        return null;
    }

    @Override
    public IIcon getBreakingIcon(Object subPart, int side) {
        return null;
    }

    @Override
    public IIcon getBrokenIcon(int side) {
        return null;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return null;
    }

    @Override
    public String getType() {
        return "";
    }
}
