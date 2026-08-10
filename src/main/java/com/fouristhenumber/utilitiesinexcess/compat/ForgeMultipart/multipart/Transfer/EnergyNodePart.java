package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyNodePart extends PartNetworkComponentBase<EnergyTransferNodeLogic> implements IWalkingComponent<Integer>
{
    protected EnergyNodePart(int side) {
        super(side);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.TRANSFER_NODE_ENERGY.get();
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
        return getBlock().getIcon(side, meta);
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
        return ConversionRegistry.EnergyNode.getName();
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
    public Integer getWalkingObject() {
        return logic.containedEnergy;
    }

    @Override
    public ForgeDirection getFacing() {
        return null;
    }
}
