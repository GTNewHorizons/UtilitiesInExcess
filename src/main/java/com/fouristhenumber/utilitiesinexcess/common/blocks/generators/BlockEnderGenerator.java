package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityEnderGenerator;

public class BlockEnderGenerator extends BlockBaseGenerator {

    public BlockEnderGenerator(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityEnderGenerator();
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
