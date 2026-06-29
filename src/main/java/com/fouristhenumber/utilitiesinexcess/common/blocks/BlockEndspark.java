package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockEndspark extends Block {

    public BlockEndspark() {
        super(Material.rock);
        setBlockName("endspark");
        setBlockTextureName("utilitiesinexcess:endspark");

        setResistance(2000F);
        setHardness(30F);
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        return 20.0f;
    }
}
