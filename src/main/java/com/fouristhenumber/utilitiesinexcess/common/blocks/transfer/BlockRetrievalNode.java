package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidRetrievalNode;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemRetrievalNode;
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
import net.minecraft.world.World;

import java.util.List;

public class BlockRetrievalNode extends BlockNodeBase
{

    public enum RetrievalNodeType
    {
        ITEM("retrieval_node_item", "retrieval_node_item_top", "retrieval_node_item_face"),
        FLUID("retrieval_node_fluid", "retrieval_node_fluid_top", "retrieval_node_fluid_face");

        private final String name;
        private final String[] textureNames;
        protected IIcon[] iicons;

        RetrievalNodeType(String name)
        {
            this(name, name);
        }

        RetrievalNodeType(String name, String... textures)
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
        public IIcon getIcon(int side, int meta) // Note that we're just using the first 3 bits here. Fourth bit is the type
        {
            if ((meta & 7) == side)
            {
                return iicons[1];
            }
            return iicons[0];
        }
    }

    public BlockRetrievalNode() {
        super();
        setBlockName("retrieval_node");
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (int i = 0; i < BlockRetrievalNode.RetrievalNodeType.values().length; i++)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

        if ((metadata & 1) == 0)
        {
            return new TileEntityItemRetrievalNode();
        }
        return new TileEntityFluidRetrievalNode();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (int i = 0; i < BlockTransferNode.TransferNodeType.values().length; i++)
        {
            BlockRetrievalNode.RetrievalNodeType.values()[i].registerIcon(reg);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return BlockRetrievalNode.RetrievalNodeType.values()[(meta & 1)].getIcon(side, meta);
    }

    @Override
    public BaseInserter getInserter(int meta) {
        return new DefaultInserter();
    }
}
