package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.render.ScaledCubeFaceIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySignificantlyShrunkChest;

public class BlockSignificantlyShrunkChest extends BlockMarginallyMaximisedChest {

    public BlockSignificantlyShrunkChest() {
        super();
        setBlockName("significantly_shrunk_chest");
        //setBlockTextureName("significantly_shrunk_chest"); // Used as prefix for icons
        setBlockBounds(0.3f, 0, 0.3f, 0.7f, 0.4f, 0.7f);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntitySignificantlyShrunkChest();
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[3];
        int i = 0;
        for (String side : sideNames) {
            IIcon icon = reg.registerIcon(String.format("%s:%s_%s", UtilitiesInExcess.MODID, getTextureName(), side));
            icons[i++] = new ScaledCubeFaceIcon(icon, this, side.equals("top"));
        }
    }

    public static class ItemBlockSignificantlyShrunkChest extends ItemBlock {

        public ItemBlockSignificantlyShrunkChest(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocalFormatted("tile.significantly_shrunk_chest.desc"));
        }
    }
}
