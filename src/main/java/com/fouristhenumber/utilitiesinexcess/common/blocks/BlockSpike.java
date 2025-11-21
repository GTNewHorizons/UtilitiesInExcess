package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.spikeRenderID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySpike;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpike extends Block {

    public BlockSpike(SpikeType spikeType, String name) {
        super(spikeType.material.getMaterial());
        this.spikeType = spikeType;
        setBlockName(name);
        setBlockTextureName("utilitiesinexcess:spike");
        setHardness(spikeType.material.getBlockHardness(null, 0, 0, 0));
        setResistance(spikeType.material.getExplosionResistance(null));
        setHarvestLevel(spikeType.material.getHarvestTool(0), spikeType.material.getHarvestLevel(0));
    }

    private static final ThreadLocal<ItemStack> cachedDrop = new ThreadLocal<>();
    private final SpikeType spikeType;

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

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list,
        Entity collider) {
        AxisAlignedBB fullBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        if (fullBox.intersectsWith(mask)) {
            list.add(fullBox);
        }

        if (collider instanceof EntityLivingBase) {
            double inset = 0.05;
            AxisAlignedBB damageBox = AxisAlignedBB
                .getBoundingBox(x + inset, y, z + inset, x + 1 - inset, y + 1, z + 1 - inset);

            if (damageBox.intersectsWith(mask)) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntitySpike spike) {
                    spike.damageEntity(collider);
                }
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

    public SpikeType getSpikeType() {
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

    private final static IIcon[] icons = new IIcon[4];

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        icons[0] = reg.registerIcon("utilitiesinexcess:models/woodSpike");
        icons[1] = reg.registerIcon("utilitiesinexcess:models/ironSpike");
        icons[2] = reg.registerIcon("utilitiesinexcess:models/goldSpike");
        icons[3] = reg.registerIcon("utilitiesinexcess:models/diamondSpike");
        super.registerBlockIcons(reg);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return switch (spikeType) {
            case WOOD -> icons[0];
            case IRON -> icons[1];
            case GOLD -> icons[2];
            case DIAMOND -> icons[3];
        };
    }

    public enum SpikeType {

        WOOD(Blocks.planks, 0.5F),
        IRON(Blocks.iron_block, 6F),
        GOLD(Blocks.gold_block, 4F),
        DIAMOND(Blocks.diamond_block, 7F);

        final Block material;
        final float damage;

        SpikeType(Block material, float damage) {
            this.material = material;
            this.damage = damage;
        }
    }

    public static class ItemSpike extends ItemBlock {

        private final float attackDamage;

        public ItemSpike(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);

            BlockSpike spike = (BlockSpike) block;
            this.attackDamage = spike.getSpikeType().damage;
        }

        @Override
        public int getItemEnchantability() {
            return 10;
        }

        @Override
        public boolean isItemTool(ItemStack p_77616_1_) {
            return true;
        }

        @Override
        public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
            Multimap<String, AttributeModifier> map = super.getAttributeModifiers(stack);

            map.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(field_111210_e, "Spike Weapon modifier", this.attackDamage, 0));
            return map;
        }
    }
}
