package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class BlockMetaOverrideWorld extends MetaOverrideWorld
{
    private final Block block;
    public BlockMetaOverrideWorld(IBlockAccess world, int x, int y, int z, int meta, Block block) {
        super(world, x, y, z, meta);
        this.block = block;
    }

    @Override
    public Block getBlock(int x, int y, int z)
    {
        if (x == this.x && y == this.y && z == this.z)
        {
            return block;
        }
        return world.getBlock(x, y, z);
    }
}
