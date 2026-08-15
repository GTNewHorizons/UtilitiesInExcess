package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;

import java.util.List;

public class BlockTransferNode extends BlockNodeBase {

    public enum TransferNodeType
    {
        ITEM("transfer_node_item", "transfer_node_item_top", "transfer_node_item_face"),
        FLUID("transfer_node_fluid", "transfer_node_fluid_top", "transfer_node_fluid_face");

        private final String name;
        private final String[] textureNames;
        protected IIcon[] iicons;

        TransferNodeType(String name)
        {
            this(name, name);
        }

        TransferNodeType(String name, String... textures)
        {
            this.name = name;
            this.textureNames = textures;
            this.iicons = new IIcon[this.textureNames.length];
        }

        public void registerIcon(IIconRegister reg)
        {
            for (int i = 0; i < iicons.length; i++)
            {
                this.iicons[i] = reg.registerIcon("utilitiesinexcess:" + textureNames[i]);
            }
        }

        public String getName()
        {
            return name;
        }

        // Meta corresponds to the ForgeDirection that is pointing toward the target container.
        // So if the first three bits are equal to 5 then the target direction is east.
        public IIcon getIcon(int side, int meta)
        {
            if (BlockNodeBase.getFacingOrdinal(meta) == side)
            {
                return iicons[1];
            }
            return iicons[0];
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

        if (metadata >> 3 == 0)
        {
            return new TileEntityItemTransferNode();
        }
        return new TileEntityFluidTransferNode();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (int i = 0; i < TransferNodeType.values().length; i++)
        {
            TransferNodeType.values()[i].registerIcon(reg);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return TransferNodeType.values()[meta & 1].getIcon(side, meta);
    }
}
