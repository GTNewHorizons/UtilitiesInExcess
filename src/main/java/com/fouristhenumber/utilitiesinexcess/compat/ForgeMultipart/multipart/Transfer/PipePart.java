package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.block.Block;

public class PipePart extends PartNetworkComponentBase
{
    public PipePart(int meta) {
        super(meta);
    }

    @Override
    public Block getBlock() {
        return null;
    }

    @Override
    protected NetworkLogic<? extends ITransferNetworkComponent> getLogic() {
        return null;
    }

    @Override
    public String getType() {
        return ConversionRegistry.Pipe.getName();
    }

    @Override
    public void render(Vector3 position, int pass) {

    }

    @Override
    public Cuboid6 getBounds() {
        return null;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return null;
    }
}
