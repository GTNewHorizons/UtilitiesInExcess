package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import cofh.api.energy.IEnergyHandler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFilterPipe;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public enum PipeType
{
    TRANSFER("transfer_pipe"),
    CROSSOVER("crossover_pipe") {
        @Override
        public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
        {
            return 1 << fromDirection.getOpposite().ordinal();
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
        public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
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
    SORTING("sorting_pipe"),
    MODSORTING("mod_sorting_pipe"),
    RATIONING("rationing_pipe"),
    HYPERRATIONING("hyper_rationing_pipe"),
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

    public static PipeType fromMeta(int meta) {
        if (meta >= 0 && meta < PipeType.values().length)
        {
            return PipeType.values()[meta];
        }
        return TRANSFER;
    }

    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
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
            Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);

            boolean connects;
            if (block instanceof BlockTransferBase transferBase)
            {
                connects = transferBase.acceptsConnectionFrom(dir.getOpposite());
            }
            else
            {
                TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                connects = te instanceof IFluidHandler || te instanceof IInventory || te instanceof IEnergyHandler;
            }
            if (connects)
            {
                mask |= 1 << dir.ordinal();
            }
        }
        return mask;
    }
}
