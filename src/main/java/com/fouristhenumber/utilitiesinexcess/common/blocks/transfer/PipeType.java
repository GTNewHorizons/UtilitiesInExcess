package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFilterPipe;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.ModSortedInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.RationedInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.SortedInserter;
import minetweaker.api.block.IBlock;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic.isValidConnectable;

public enum PipeType
{
    TRANSFER("transfer_pipe"),
    CROSSOVER("crossover_pipe")
    {
        @Override
        public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
        {
            return 1 << fromDirection.getOpposite().ordinal();
        }

        @Override
        public boolean acceptsConnectionFrom(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
        {
            ForgeDirection opp = dir.getOpposite();
            return isValidConnectable(world, x + opp.offsetX, y + opp.offsetY, z + opp.offsetZ, opp);
        }

        // TODO Some issue with cross chunk rendering for updating of crossover pipes.
        @Override
        public int getConnectionMask(IBlockAccess world, int x, int y, int z)
        {
            int mask = 0;
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if (getConnection(world, x, y, z, dir))
                {
                    mask |= 1 << dir.ordinal();
                }
            }
            return mask;
        }

        @Override
        public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
        {
            ForgeDirection opp = dir.getOpposite();
            return isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir) &&
                isValidConnectable(world, x + opp.offsetX, y + opp.offsetY, z + opp.offsetZ, opp);
        }
    },
    FILTER("filter_pipe", "filter_pipe_0", "filter_pipe_1", "filter_pipe_2")
    {
        @Override
        public IIcon getIcon(int side)
        {
            switch (side)
            {
                case 0: // bottom
                case 1: // top
                    return this.iicons[0];

                case 2:
                case 3:
                    return this.iicons[1];

                case 4:
                case 5:
                    return this.iicons[2];

                default:
                    return this.iicons[0];
            }
        }

        @Override
        public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
        {
            if (world.getTileEntity(x, y, z) instanceof TileEntityFilterPipe filterPipe)
            {
                Object walkingObject = walkingComponent.getWalkingObject();
                if (walkingObject instanceof ItemStack stack) {
                    return filterPipe.getValidMask(fromDirection, stack);
                }
            }
            return 0;
        }
    },
    SORTING("sorting_pipe")
    {

        @Override
        public BaseInserter getInserter()
        {
            return new SortedInserter();
        }
    },
    MODSORTING("mod_sorting_pipe")
    {
        @Override
        public BaseInserter getInserter()
        {
            return new ModSortedInserter();
        }
    },
    RATIONING("rationing_pipe")
    {
        @Override
        public BaseInserter getInserter()
        {
            return new RationedInserter(64);
        }
    },
    HYPERRATIONING("hyper_rationing_pipe")
    {
        @Override
        public BaseInserter getInserter()
        {
            return new RationedInserter(1);
        }
    },
    ENERGY("energy_pipe"),
    ENERGYEXTRACTION("energy_extraction_pipe");

    private final String name;
    private final String[] textureNames;
    protected IIcon[] iicons;

    PipeType(String name)
    {
        this(name, name);
    }

    PipeType(String name, String... textures)
    {
        this.name = name;
        this.textureNames = textures;
        this.iicons = new IIcon[this.textureNames.length];
    }

    public int getMeta() {
        return this.ordinal();
    }

    public String getName() {
        return name;
    }

    public String[] getTextureNames()
    {
        return textureNames;
    }

    public void registerIcon(IIconRegister reg)
    {
        for (int i = 0; i < iicons.length; i++)
        {
            this.iicons[i] = reg.registerIcon("utilitiesinexcess:" + textureNames[i]);
        }
    }

    public IIcon getIcon(int side)
    {
        return iicons[0];
    }

    public static PipeType fromMeta(int meta)
    {
        if (meta >= 0 && meta < PipeType.values().length)
        {
            return PipeType.values()[meta];
        }
        return TRANSFER;
    }

    public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        if (fromDirection != ForgeDirection.UNKNOWN)
        {
            return 0b111111 ^ (1 << fromDirection.ordinal());
        }
        return 0b111111;
    }

    public int getConnectionMask(IBlockAccess world, int x, int y, int z)
    {
        int mask = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (getConnection(world, x, y, z, dir))
            {
                mask |= 1 << dir.ordinal();
            }
        }
        return mask;
    }

    public boolean acceptsConnectionFrom(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return true;
    }

    public BaseInserter getInserter() {
        return new DefaultInserter();
    }

    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
    }
}
