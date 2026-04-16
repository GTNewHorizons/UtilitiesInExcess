package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static net.minecraftforge.common.util.ForgeDirection.*;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class BlockBlackoutCurtains extends Block {

    public BlockBlackoutCurtains() {
        super(Material.cloth);
        setBlockName("blackout_curtains");
        setBlockTextureName("utilitiesinexcess:blackout_curtains");
        setLightOpacity(8);
        setHardness(0);
        setResistance(0);
        setStepSound(soundTypeCloth);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return UtilitiesInExcess.blackoutCurtainsRenderID;
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {}

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        float bN = 7 / 16f;
        float bW = 7 / 16f;
        float bS = 9 / 16f;
        float bE = 9 / 16f;
        boolean connNorth = this.canConnectTo(worldIn, x, y, z - 1, NORTH);
        boolean connSouth = this.canConnectTo(worldIn, x, y, z + 1, SOUTH);
        boolean connWest = this.canConnectTo(worldIn, x - 1, y, z, WEST);
        boolean connEast = this.canConnectTo(worldIn, x + 1, y, z, EAST);

        if (connWest || connEast || connNorth || connSouth) {
            if (connNorth) {
                bN = 0.0F;
            }
            if (connWest) {
                bW = 0.0F;
            }
            if (connSouth) {
                bS = 1.0F;
            }
            if (connEast) {
                bE = 1.0F;
            }
        } else {
            bN = 0.0F;
            bW = 0.0F;
            bS = 1.0F;
            bE = 1.0F;
        }

        this.setBlockBounds(bW, 0.0F, bN, bE, 1.0F, bS);
    }

    public final boolean canConnectToBlock(Block block) {
        return block.func_149730_j() || block instanceof BlockBlackoutCurtains;
    }

    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        return canConnectToBlock(world.getBlock(x, y, z)) || world.isSideSolid(x, y, z, dir.getOpposite(), false);
    }
}
