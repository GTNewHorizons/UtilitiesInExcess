package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityCrossoverPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityEnergyExtractionPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityEnergyPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityFilterPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityHyperRationingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityModSortingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityRationingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntitySortingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityTransferPipe;
import com.fouristhenumber.utilitiesinexcess.utils.ITileFactory;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public enum PipeType
{
    TRANSFER("transfer_pipe", TileEntityTransferPipe::new),
    CROSSOVER("crossover_pipe", TileEntityCrossoverPipe::new),
    FILTER("filter_pipe", TileEntityFilterPipe::new, "filter_pipe_0", "filter_pipe_1", "filter_pipe_2")
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
    },
    SORTING("sorting_pipe", TileEntitySortingPipe::new),
    MODSORTING("mod_sorting_pipe", TileEntityModSortingPipe::new),
    RATIONING("rationing_pipe", TileEntityRationingPipe::new),
    HYPERRATIONING("hyper_rationing_pipe", TileEntityHyperRationingPipe::new),
    ENERGY("energy_pipe", TileEntityEnergyPipe::new),
    ENERGYEXTRACTION("energy_extraction_pipe", TileEntityEnergyExtractionPipe::new);

    private final String name;
    private final ITileFactory factory;
    private final String[] textureNames;
    protected IIcon[] iicons;

    PipeType(String name, ITileFactory factory)
    {
        this(name, factory, name);
    }

    PipeType(String name, ITileFactory factory, String... textures)
    {
        this.name = name;
        this.textureNames = textures;
        this.factory = factory;
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

    public TileEntity createTileEntity() {
        return factory.create();
    }

    public static PipeType fromMeta(int meta) {
        if (meta >= 0 && meta < PipeType.values().length)
        {
            return PipeType.values()[meta];
        }
        return TRANSFER;
    }
}
