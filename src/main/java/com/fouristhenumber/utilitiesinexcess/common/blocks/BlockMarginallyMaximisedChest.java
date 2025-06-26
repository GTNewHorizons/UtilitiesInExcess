package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityMarginallyMaximisedChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMarginallyMaximisedChest extends BlockContainer {

    private final Random random = new Random();

    public BlockMarginallyMaximisedChest() {
        super(Material.wood);
        setBlockName("marginally_maximised_chest");
        setBlockTextureName("scaled_chest"); // Used as prefix for icons
        setHardness(2.5F);
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
            dropContent(0, inv, worldIn, te.xCoord, te.yCoord, te.zCoord);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public void dropContent(int newSize, IInventory chest, World world, int x, int y, int z) {
        for (int l = newSize; l < chest.getSizeInventory(); l++) {
            ItemStack itemstack = chest.getStackInSlot(l);
            if (itemstack == null) {
                continue;
            }
            float f = random.nextFloat() * 0.8F + 0.1F;
            float f1 = random.nextFloat() * 0.8F + 0.1F;
            float f2 = random.nextFloat() * 0.8F + 0.1F;
            while (itemstack.stackSize > 0) {
                int i1 = random.nextInt(21) + 10;
                if (i1 > itemstack.stackSize) {
                    i1 = itemstack.stackSize;
                }
                itemstack.stackSize -= i1;
                EntityItem entityitem = new EntityItem(
                    world,
                    (float) x + f,
                    (float) y + (newSize > 0 ? 1 : 0) + f1,
                    (float) z + f2,
                    new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage()));
                float f3 = 0.05F;
                entityitem.motionX = (float) random.nextGaussian() * f3;
                entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) random.nextGaussian() * f3;
                if (itemstack.hasTagCompound()) {
                    entityitem.getEntityItem()
                        .setTagCompound(
                            (NBTTagCompound) itemstack.getTagCompound()
                                .copy());
                }
                world.spawnEntityInWorld(entityitem);
            }
        }
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

    public static class ItemBlockMarginallyMaximisedChest extends ItemBlock {

        public ItemBlockMarginallyMaximisedChest(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocalFormatted("tile.marginally_maximised_chest.desc1"));
            tooltip.add(StatCollector.translateToLocalFormatted("tile.marginally_maximised_chest.desc2"));
        }
    }
}
