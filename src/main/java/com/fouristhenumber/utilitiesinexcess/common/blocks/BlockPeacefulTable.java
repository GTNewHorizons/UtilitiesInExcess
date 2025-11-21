package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPeacefulTable;

public class BlockPeacefulTable extends BlockContainer {

    public BlockPeacefulTable() {
        super(Material.wood);
        setBlockTextureName("utilitiesinexcess:peaceful_table");
        setBlockName("peaceful_table");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPeacefulTable();
    }
}
