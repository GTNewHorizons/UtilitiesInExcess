package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemInvertedIngot extends Item {

    public ItemInvertedIngot() {
        this.setUnlocalizedName("inverted_ingot");
        this.setTextureName("utilitiesinexcess:inverted_ingot");
        this.setMaxStackSize(1);
    }

    public static final DamageSource INVERTED_INGOT = (new DamageSource("invertedIngot")).setDamageBypassesArmor();

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean p_77663_5_) {
        if (entityIn instanceof EntityItem item) item.setDead();
        if (!(entityIn instanceof EntityPlayer player)) return;

        if (!(player.openContainer instanceof ContainerWorkbench) || checkImplosion(stack, player, worldIn)) {
            player.inventory.setInventorySlotContents(slot, null);
            player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
        }

        super.onUpdate(stack, worldIn, entityIn, slot, p_77663_5_);
    }

    public static boolean checkImplosion(ItemStack item, EntityPlayer player, World world) {
        if (item.hasTagCompound()) {
            long timeCreated = item.getTagCompound()
                .getInteger("TimeCreated");
            return (world.isRemote && world.getTotalWorldTime() >= timeCreated + 200);
        }
        return false;
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
        if (itemStack != null && itemStack.getItemDamage() == 0) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("TimeCreated", world.getTotalWorldTime());
            itemStack.setTagCompound(nbt);
        }
        super.onCreated(itemStack, world, player);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.1"));
        if (stack.hasTagCompound()) {
            double time = (double) (player.worldObj.getTotalWorldTime() - stack.getTagCompound()
                .getLong("TimeCreated")) / 20;
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted("item.inverted_ingot.desc.2", formatNumber(Math.max(0, 10 - time))));
        } else {
            tooltip.add(StatCollector.translateToLocalFormatted("item.inverted_ingot.desc.2", 10));
        }
        tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.3"));
        tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.4"));
        tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.5"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (stack.getItemDamage() == 0) return 1;
        return 64;
    }
}
