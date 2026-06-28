package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import net.minecraftforge.common.util.ForgeDirection;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.flatNodeRenderId;

public class BlockTransferNode extends BlockNodeBase {

    enum TransferNodeType
    {
        ITEM("item_transfer_node", "item_transfer_node_top", "item_transfer_node_face"),
        FLUID("fluid_transfer_node", "fluid_transefer_node_top", "fluid_transfer_node_face");

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
    }

    public BlockTransferNode() {
        super();
        setBlockName("transfer_node");
    }

    @Override
    public int getRenderType() {
        return flatNodeRenderId;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

        if (metadata >> 3 == 0)
        {
            return new TileEntityItemTransferNode(ForgeDirection.getOrientation(metadata & 7));
        }
        return new TileEntityFluidTransferNode(ForgeDirection.getOrientation(metadata & 7));
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
}
