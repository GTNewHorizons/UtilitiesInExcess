package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySmartPump;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSmartPump extends BlockContainer {

    public BlockSmartPump() {
        super(Material.iron);
        setBlockName("smart_pump");
        setBlockTextureName("utilitiesinexcess:smart_pump");
    }

    IIcon sides;
    IIcon top;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        sides = reg.registerIcon("utilitiesinexcess:smart_pump");
        top = reg.registerIcon("utilitiesinexcess:smart_pump_top");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int s, int meta) {
        if (s == ForgeDirection.UP.ordinal() || s == ForgeDirection.DOWN.ordinal()) {
            return top;
        } else {
            return sides;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySmartPump();
    }
}
