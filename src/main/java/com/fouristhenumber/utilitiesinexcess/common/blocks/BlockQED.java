package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityQED;

public class BlockQED extends BlockContainer {

    public BlockQED() {
        super(Material.iron);
        setBlockName("qed");
        setBlockTextureName("utilitiesinexcess:qed");
        setHardness(5.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityQED();
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (!worldIn.isRemote) {
            TileEntityQED qed = (TileEntityQED) worldIn.getTileEntity(x, y, z);
            qed.scan();
        }
        super.onBlockAdded(worldIn, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (!worldIn.isRemote) {
            GuiFactories.tileEntity()
                .open(player, x, y, z);
        }
        return true;
    }
}
