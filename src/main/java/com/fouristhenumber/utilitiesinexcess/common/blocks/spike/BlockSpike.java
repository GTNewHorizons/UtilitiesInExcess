package com.fouristhenumber.utilitiesinexcess.common.blocks.spike;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.spikeRenderID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpike extends Block {

    public BlockSpike(SpikeDamageSource.spikeTypes spikeType, String name) {
        super(Material.wood);
        this.spikeType = spikeType;
        setBlockName(name);
        setBlockTextureName("utilitiesinexcess:spike");
        setHardness(1.5F);
    }

    private static final ThreadLocal<ItemStack> cachedDrop = new ThreadLocal<>();
    private final SpikeDamageSource.spikeTypes spikeType;

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileEntitySpike(spikeType);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntitySpike spike) {
                ItemStack copy = stack.copy();
                copy.stackSize = 1;
                spike.setFakeWeapon(copy);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof TileEntitySpike spike) {
                spike.damageEntity(entity);
            }
        }
    }

    // Cache the itemStack held by the TE before it's removed
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntitySpike spike) {
            ItemStack toDrop = spike.getFakeWeapon();
            if (toDrop != null) {
                cachedDrop.set(toDrop.copy());
            } else {
                cachedDrop.remove();
            }
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    // Drop the cached ItemStack
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        ItemStack drop = cachedDrop.get();

        drops.add(drop != null ? drop : new ItemStack(this));

        cachedDrop.remove();
        return drops;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float shrink = 0.005F;
        return AxisAlignedBB
            .getBoundingBox(x + shrink, y + shrink, z + shrink, x + 1 - shrink, y + 1 - shrink, z + 1 - shrink);
    }

    public SpikeDamageSource.spikeTypes getSpikeType() {
        return spikeType;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return spikeRenderID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(0, 0);
    }

    public static class ItemSpike extends ItemBlock {

        public ItemSpike(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);
        }

        @Override
        public int getItemEnchantability() {
            return 10;
        }

        @Override
        public boolean isItemTool(ItemStack p_77616_1_) {
            return true;
        }

        // Cancel default attacks and use spike damage source directly so that custom behavior applies
        @Override
        public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
            if (!player.worldObj.isRemote && entity instanceof EntityLivingBase) {
                DamageSource source = new SpikeDamageSource("ue_spike", stack, SpikeDamageSource.spikeTypes.WOOD);
                entity.attackEntityFrom(source, 1.0F);
            }

            return true;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocal("tile.spike.desc"));
        }
    }
}
