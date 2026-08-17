package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

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
        return ConversionRegistry.ItemTransferNode.getName();
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    @Override
    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        return false;
    }

    // Needs to look at
    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        return true;
    }
}
