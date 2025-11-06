package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

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

        // Destroy if not in crafting table
        if (!(player.openContainer instanceof ContainerWorkbench)) {
            player.inventory.setInventorySlotContents(slot, null);
            player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
        }

        if (stack.hasTagCompound()) {
            int timer = stack.getTagCompound().getInteger("ExplosionTimer");
            if (timer > 0) {
                timer--;
                stack.getTagCompound().setInteger("ExplosionTimer", timer);
            } else if (!worldIn.isRemote) {
                player.inventory.setInventorySlotContents(slot, null);
                player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
            }
        }
        super.onUpdate(stack, worldIn, entityIn, slot, p_77663_5_);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (stack.hasTagCompound()) {
            double time = (double) stack.getTagCompound().getInteger("ExplosionTimer") / 20;

            tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.1"));
            tooltip.add(StatCollector.translateToLocalFormatted("item.inverted_ingot.desc.2", formatNumber(time)));
            tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.3"));
            tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.4"));
            tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.5"));
        }
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }
}
