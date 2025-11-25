package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRadicallyReducedChest;

public class BlockRadicallyReducedChest extends BlockSignificantlyShrunkChest {

    public BlockRadicallyReducedChest() {
        super();
        setBlockName("radically_reduced_chest");
        setBlockBounds(0.4f, 0, 0.4f, 0.6f, 0.2f, 0.6f);
        setHardness(1.5F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityRadicallyReducedChest();
    }
}
