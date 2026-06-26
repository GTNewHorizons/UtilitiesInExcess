package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMagicWood extends Block {

    public BlockMagicWood() {
        super(Material.wood);
        setBlockName("magic_wood");
        setBlockTextureName("utilitiesinexcess:magic_wood");

        setResistance(30.0f);
        setHardness(0.45f);
        setStepSound(soundTypeWood);
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        return 5.0f;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 5;
    }
}
