package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySmartPump;

public class BlockSmartPump extends BlockContainer {

    public BlockSmartPump() {
        super(Material.iron);
        setBlockName("smart_pump");
        setBlockTextureName("utilitiesinexcess:smart_pump");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySmartPump();
    }
}
