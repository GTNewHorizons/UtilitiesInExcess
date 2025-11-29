package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderMarker;
import com.fouristhenumber.utilitiesinexcess.utils.DirectionUtil;

public class BlockEnderMarker extends BlockContainer {

    public BlockEnderMarker() {
        super(Material.iron);
        setBlockName("ender_marker");
        setBlockTextureName("utilitiesinexcess:ender_marker");
        setBlockBounds(7F / 16F, 0F / 16F, 7F / 16F, 9F / 16F, 13.5F / 16F, 9F / 16F);
        setLightOpacity(0);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        if (worldIn.isRemote) return;
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderMarker marker) {
            marker.checkForAlignedMarkers();
        }
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        this.setBlockBounds(7F / 16F, 0F / 16F, 7F / 16F, 9F / 16F, 13.5F / 16F, 9F / 16F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
        if (worldIn.isRemote) return;
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderMarker marker) {
            marker.teardownConnections();
        }
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
        this.onBlockDestroyedByPlayer(worldIn, x, y, z, 0);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (!worldIn.isRemote && te instanceof TileEntityEnderMarker marker) {
            marker.checkForAlignedMarkers();
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        for (ForgeDirection dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            if ((meta & (1 << (dir.ordinal() - 2))) != 0) {
                for (int i = 0; i < 7; i++) {
                    worldIn.spawnParticle(
                        "reddust",
                        x + 0.5D + ((double) dir.offsetX / 4) + dir.offsetX * random.nextDouble(),
                        y + 0.7D,
                        z + 0.5D + ((double) dir.offsetZ / 4) + dir.offsetZ * random.nextDouble(),
                        0.5D + ((double) dir.offsetX / 2) * i,
                        0.0D,
                        0.5D + ((double) dir.offsetZ / 2) * i);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnderMarker();
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
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }
}
