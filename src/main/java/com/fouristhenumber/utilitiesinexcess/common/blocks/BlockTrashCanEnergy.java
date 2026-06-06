package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanEnergy;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

public class BlockTrashCanEnergy extends BlockContainer {

    public BlockTrashCanEnergy() {
        super(Material.rock);
        setBlockName("trash_can_energy");
        setBlockTextureName("utilitiesinexcess:trash_can_energy");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTrashCanEnergy();
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
