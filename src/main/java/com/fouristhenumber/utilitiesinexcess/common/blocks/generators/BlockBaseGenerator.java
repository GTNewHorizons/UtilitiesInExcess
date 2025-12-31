package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityBaseGenerator;

public abstract class BlockBaseGenerator extends BlockContainer {

    public int multiplier;

    protected BlockBaseGenerator(String id, int mult) {
        super(Material.iron);
        multiplier = mult;
        setBlockName(id);
        setBlockTextureName("utilitiesinexcess:generators/" + id);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            GuiFactories.tileEntity()
                .open(playerIn, x, y, z);
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityBaseGenerator generator) {
            generator.onNeighborBlockChange();
        }
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }
}
