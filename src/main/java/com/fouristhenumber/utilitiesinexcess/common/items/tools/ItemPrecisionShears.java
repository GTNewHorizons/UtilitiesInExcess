package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

public class ItemPrecisionShears extends ItemShears {
    private final float efficiencyOnProperMaterial;

    private static final String COOLDOWN_NBT_TAG = "uie:cooldown";
    private static final int COOLDOWN_TICKS = 20;

    private IIcon cooldownIcon;

    public ItemPrecisionShears() {
        setTextureName("utilitiesinexcess:precision_shears");
        setUnlocalizedName("precision_shears");

        setMaxDamage(0);
        setMaxStackSize(1);

        int harvestLevel = ToolMaterial.STONE.getHarvestLevel();
        efficiencyOnProperMaterial = ToolMaterial.STONE.getEfficiencyOnProperMaterial();

        setHarvestLevel("pickaxe", harvestLevel);
        setHarvestLevel("axe", harvestLevel);
        setHarvestLevel("shovel", harvestLevel);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            int cooldown = nbt.getInteger(COOLDOWN_NBT_TAG);
            if (cooldown > 0) {
                nbt.setInteger(COOLDOWN_NBT_TAG, cooldown - 1);
                System.out.println("Lowered cooldown to: " + (cooldown - 1));
                stack.setTagCompound(nbt);
            }
        }
        super.onUpdate(stack, worldIn, entityIn, p_77663_4_, p_77663_5_);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ) {
        if (player.isSneaking()) {
            NBTTagCompound nbt = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
            if (nbt.getInteger(COOLDOWN_NBT_TAG) == 0) {
                Block block = world.getBlock(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);
                int harvestLevel = block.getHarvestLevel(meta);
                if (harvestLevel <= 1) {
                    // TODO: Get drops from things like chests
                    ArrayList<ItemStack> drops = block.getDrops(world, x, y, z, meta, 0);
                    for (ItemStack drop : drops) {
                        if (!player.inventory.addItemStackToInventory(drop)) {
                            player.entityDropItem(drop, 0.0f);
                        }
                    }
                    world.setBlockToAir(x, y, z);

                    nbt.setInteger(COOLDOWN_NBT_TAG, COOLDOWN_TICKS);
                    System.out.println("Set cooldown to " + COOLDOWN_TICKS);

                    itemStack.setTagCompound(nbt);

                    // TODO: Spawn smoke particles
                }
            }
        }
        return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (ForgeHooks.isToolEffective(itemstack, block, metadata)) {
            return efficiencyOnProperMaterial;
        } else {
            return super.getDigSpeed(itemstack, block, metadata);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.2"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.3"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        cooldownIcon = register.registerIcon("utilitiesinexcess:precision_shears_cooldown");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().getInteger(COOLDOWN_NBT_TAG) > 0) {
                System.out.println("Showing cooldown sprite");
                return cooldownIcon;
            }
        }
        return super.getIcon(stack, pass);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return super.getIcon(stack, renderPass, player, usingItem, useRemaining);
    }
}
