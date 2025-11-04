package com.fouristhenumber.utilitiesinexcess.common.blocks.spike;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.spikeRenderID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpike extends Block {

    public BlockSpike(spikeTypes spikeType, String name) {
        super(spikeType.material.getMaterial());
        this.spikeType = spikeType;
        setBlockName(name);
        setBlockTextureName("utilitiesinexcess:spike");
        setHardness(spikeType.material.getBlockHardness(null, 0, 0, 0));
        setResistance(spikeType.material.getExplosionResistance(null));
        setHarvestLevel(spikeType.material.getHarvestTool(0), spikeType.material.getHarvestLevel(0));
    }

    private static final ThreadLocal<ItemStack> cachedDrop = new ThreadLocal<>();
    private final spikeTypes spikeType;

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

    public spikeTypes getSpikeType() {
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
        return spikeType.material.getIcon(0, 0);
    }

    public enum spikeTypes {

        WOOD(Blocks.planks),
        IRON(Blocks.iron_block),
        GOLD(Blocks.gold_block),
        DIAMOND(Blocks.diamond_block);

        final Block material;

        spikeTypes(Block material) {
            this.material = material;
        }
    }

    public static class ItemSpike extends ItemBlock {

        private final float attackDamage;

        public ItemSpike(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);

            BlockSpike spike = (BlockSpike) block;
            switch (spike.getSpikeType()) {
                case WOOD:
                    this.attackDamage = 2.0F;
                    break;
                case IRON:
                    this.attackDamage = 6.0F;
                    break;
                case GOLD:
                    this.attackDamage = 4.0F;
                    break;
                case DIAMOND:
                    this.attackDamage = 7.0F;
                    break;
                default:
                    this.attackDamage = 1.0F;
            }
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
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            switch (((BlockSpike) field_150939_a).getSpikeType()) {
                case WOOD:
                    tooltip.add(StatCollector.translateToLocal("tile.woodSpike.desc"));
                    break;
                case IRON:
                    tooltip.add(StatCollector.translateToLocal("tile.ironSpike.desc"));
                    break;
                case GOLD:
                    tooltip.add(StatCollector.translateToLocal("tile.goldSpike.desc"));
                    break;
                case DIAMOND:
                    tooltip.add(StatCollector.translateToLocal("tile.diamondSpike.desc"));
                    break;
                default:
                    break;
            }
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
