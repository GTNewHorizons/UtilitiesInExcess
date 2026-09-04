package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;

import java.util.List;

public class BlockTransferNode extends BlockNodeBase {

    public enum TransferNodeType
    {
        ITEM("transfer_node_item"),
        FLUID("transfer_node_fluid");

        private final String name;

        TransferNodeType(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    public BlockTransferNode() {
        super();
        setBlockName("transfer_node");
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return new DefaultInserter();
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (int i = 0; i < TransferNodeType.values().length; i++)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

        if (BlockNodeBase.getType(metadata) == 0)
        {
            return new TileEntityItemTransferNode();
        }
        return new TileEntityFluidTransferNode();
    }
}
