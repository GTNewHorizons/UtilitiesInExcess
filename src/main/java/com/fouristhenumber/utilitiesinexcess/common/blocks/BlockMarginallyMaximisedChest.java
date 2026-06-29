package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.dropItemsFromIInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityMarginallyMaximisedChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMarginallyMaximisedChest extends BlockContainer {

    public BlockMarginallyMaximisedChest() {
        super(Material.wood);
        setBlockName("marginally_maximised_chest");
        setBlockTextureName("scaled_chest"); // Used as prefix for icons
        setHardness(2.5F);
        setResistance(2.5F);
        setStepSound(soundTypeWood);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityMarginallyMaximisedChest();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        byte chestFacing = 0;
        int facing = MathHelper.floor_double((double) ((placer.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        if (facing == 0) {
            chestFacing = 2;
        }
        if (facing == 1) {
            chestFacing = 5;
        }
        if (facing == 2) {
            chestFacing = 3;
        }
        if (facing == 3) {
            chestFacing = 4;
        }
        worldIn.setBlockMetadataWithNotify(x, y, z, chestFacing, 3);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityMarginallyMaximisedChest chest) {
            player.displayGUIChest(chest);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof IInventory inv) {
            dropItemsFromIInventory(0, inv, worldIn, te.xCoord, te.yCoord, te.zCoord);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof IInventory inv) {
            return Container.calcRedstoneFromInventory(inv);
        }
        return 0;
    }

    protected static final String[] sideNames = { "top", "front", "side" };

    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[3];
        int i = 0;
        for (String side : sideNames) {
            icons[i++] = reg.registerIcon(String.format("%s:%s_%s", UtilitiesInExcess.MODID, getTextureName(), side));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side <= 1) {
            // Top or Bottom
            return icons[0];
        } else if (side == meta) {
            // Front
            return icons[1];
        } else if (meta == 0 && side == 3) {
            // Front and rendering in an inventory (no face set)
            return icons[1];
        } else {
            // Side
            return icons[2];
        }
    }
}
