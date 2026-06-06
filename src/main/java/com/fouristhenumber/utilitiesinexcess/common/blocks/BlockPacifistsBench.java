package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPacifistsBench;

public class BlockPacifistsBench extends BlockContainer {

    public BlockPacifistsBench() {
        super(Material.wood);
        setBlockTextureName("utilitiesinexcess:pacifists_bench");
        setBlockName("pacifists_bench");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPacifistsBench();
    }
}
