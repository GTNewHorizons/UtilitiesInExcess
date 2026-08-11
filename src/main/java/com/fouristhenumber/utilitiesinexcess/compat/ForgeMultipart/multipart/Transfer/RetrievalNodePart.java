package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class RetrievalNodePart<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends PartNetworkComponentBase<T> implements IWalkingComponent<Integer>
{
    public RetrievalNodePart(int meta) {
        super(meta);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.RETRIEVAL_NODE.get();
    }

    @Override
    protected T getLogic() {
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
        return ConversionRegistry.RetrievalNode.getName();
    }

    @Override
    public Integer getWalkingObject() {
        return 0;
    }

    @Override
    public ForgeDirection getFacing() {
        return null;
    }

    @Override
    public int getMeta() {
        return 0;
    }
}
