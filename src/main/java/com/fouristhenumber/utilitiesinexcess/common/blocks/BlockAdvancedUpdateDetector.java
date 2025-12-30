package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static net.minecraft.util.Facing.facings;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityAdvancedBlockUpdateDetector;

public class BlockAdvancedUpdateDetector extends BlockContainer {

    private IIcon iconInactive;
    private IIcon iconActive;

    public BlockAdvancedUpdateDetector() {
        super(Material.rock);
        setBlockName("advanced_block_update_detector");
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        iconInactive = reg.registerIcon("utilitiesinexcess:advanced_block_update_detector_inactive");
        iconActive = reg.registerIcon("utilitiesinexcess:advanced_block_update_detector_active");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        boolean active = (meta) == 1;
        return active ? iconActive : iconInactive;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAdvancedBlockUpdateDetector();
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityAdvancedBlockUpdateDetector tileABUD) {
            return tileABUD.getOutputPower();
        }
        return 0;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (!player.isSneaking()) {
            return false;
        }
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityAdvancedBlockUpdateDetector tileABUD) {
            tileABUD.toggleFace(side);
            player.addChatMessage(
                new ChatComponentTranslation(
                    "chat.tile.advanced_block_update_detector.toggle",
                    facings[side],
                    tileABUD.getScanning(side)));
            return true;
        }
        return false;
    }
}
