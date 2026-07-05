package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTrueGreenscreen extends BlockContainer {

    public BlockTrueGreenscreen() {
        super(Material.sponge);
        setStepSound(soundTypeCloth);
        setBlockName("true_greenscreen");
        setHardness(0.5f);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {

    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTrueGreenscreen();
    }

    @Override
    public String getItemIconName() {
        return UtilitiesInExcess.MODID + ":true_greenscreen";
    }

    public static class TileEntityTrueGreenscreen extends TileEntity {

    }

    // Only exists to implement ITranslucentItem
    public static class ItemBlockTrueGreenscreen extends ItemBlock implements ITranslucentItem {

        public ItemBlockTrueGreenscreen(Block block) {
            super(block);
        }
    }

}
