package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInvertedIngot extends Item {

    public ItemInvertedIngot() {
        this.setTextureName("utilitiesinexcess:inverted_ingot");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public static final DamageSource INVERTED_INGOT = (new DamageSource("inverted_ingot")).setDamageBypassesArmor();

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean p_77663_5_) {
        if (stack.getItemDamage() != 0 || !stack.hasTagCompound()) return;
        if (!(entityIn instanceof EntityPlayer player)) return;

        if (!(player.openContainer instanceof ContainerWorkbench) || checkImplosion(stack, worldIn)) {
            player.inventory.setInventorySlotContents(slot, null);
            player.closeScreen();
            player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
        }

        super.onUpdate(stack, worldIn, entityIn, slot, p_77663_5_);
    }

    public static boolean checkImplosion(ItemStack item, World world) {
        if (item.getItemDamage() == 0 && item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            int remaining = tag.getInteger("ImplosionTimer");
            if (remaining > 0) {
                tag.setInteger("ImplosionTimer", remaining - 1);
            } else {
                return (world.isRemote);
            }
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getItemDamage() == 0) return "item.inverted_ingot";
        else return "item.inverted_ingot.stable";
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (stack.getItemDamage() == 0) {
            if (InversionConfig.invertedIngotsImplode) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag == null) {
                    tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.c"));
                } else {
                    tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.1"));
                    if (stack.hasTagCompound()) {
                        double time = (double) stack.getTagCompound()
                            .getInteger("ImplosionTimer") / 20;
                        tooltip.add(
                            StatCollector.translateToLocalFormatted(
                                "item.inverted_ingot.desc.2",
                                formatNumber(Math.max(0, time))));
                    } else {
                        tooltip.add(StatCollector.translateToLocalFormatted("item.inverted_ingot.desc.2", 10));
                    }
                    tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.3"));
                    tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.4"));
                    tooltip.add(StatCollector.translateToLocal("item.inverted_ingot.desc.5"));
                }
            }
        } else {
            tooltip.add(StatCollector.translateToLocalFormatted("item.inverted_ingot.stable.desc"));
        }
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return (stack.getItemDamage() == 0) ? 1 : 64;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[2];
        icons[0] = reg.registerIcon("utilitiesinexcess:inverted_ingot");
        icons[1] = reg.registerIcon("utilitiesinexcess:inverted_ingot_stable");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }
}
