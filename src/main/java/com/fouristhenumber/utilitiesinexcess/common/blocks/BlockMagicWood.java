package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockMagicWood extends Block {

    public BlockMagicWood() {
        super(Material.wood);
        setBlockName("magic_wood");
        setBlockTextureName("utilitiesinexcess:magic_wood");

        setResistance(5F);
        setHardness(2F);
        setStepSound(soundTypeWood);
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        return 5.0f;
    }
}
