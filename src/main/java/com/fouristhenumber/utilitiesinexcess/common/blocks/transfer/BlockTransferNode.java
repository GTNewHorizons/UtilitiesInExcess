package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
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
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.flatNodeRenderID;

public class BlockTransferNode extends BlockNodeBase {

    public enum TransferNodeType
    {
        ITEM("transfer_node_item", "transfer_node_item_top", "transfer_node_item_face") {
            @Override
            public IIcon getIcon(int side, int meta) // Note that we're just using the first 3 bits here. Fourth bit is the type
            {
                int sideDependentMeta = meta & 7;
                return switch (sideDependentMeta) {
                    case 0 -> iicons[1];
                    case 1, 2, 3, 4, 5 -> iicons[0];
                    default -> null;
                };
            }
        },
        FLUID("transfer_node_fluid", "transfer_node_fluid_top", "transfer_node_fluid_face") {
            @Override
            public IIcon getIcon(int side, int meta)
            {
                int sideDependentMeta = meta & 7;
                return switch (sideDependentMeta) {
                    case 0 -> iicons[1];
                    case 1, 2, 3, 4, 5 -> iicons[0];
                    default -> null;
                };
            }
        };

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
        public abstract IIcon getIcon(int side, int meta);
    }

    public BlockTransferNode() {
        super();
        setBlockName("transfer_node");
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
    public int getRenderType()
    {
        return flatNodeRenderID;
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
        return TransferNodeType.values()[(meta >> 3)].getIcon(side, meta);
    }

    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, int metadata, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        int facing = metadata & 7;
        if (facing < 6)
        {
            mask &= ~(1 << facing);
        }
        if (fromDirection != ForgeDirection.UNKNOWN)
        {
            mask &= ~(1 << fromDirection.ordinal());
        }
        return mask;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z, int metadata)
    {
        int mask = 0;
        int facing = metadata & 7;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (dir != ForgeDirection.getOrientation(facing))
            {
                if (isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir))
                {
                    mask |= 1 << dir.ordinal();
                }
            }

        }
        return mask;
    }

    @Override
    public boolean acceptsConnectionFrom(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection direction)
    {
        return direction.ordinal() != (metadata & 7);
    }
}
