package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class ItemGlove extends Item implements IBauble {

    public ItemGlove() {
        super();
        this.setTextureName("utilitiesinexcess:glove");
        this.setUnlocalizedName("glove");
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        super.addInformation(stack, player, tooltip, p_77624_4_);

        if (!ItemConfig.shiftForDescription || GuiScreen.isShiftKeyDown()) {
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.glove.desc.1"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.glove.desc.2"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.glove.desc.3"));
            tooltip.add(
                EnumChatFormatting.AQUA + String.format(
                    StatCollector.translateToLocal("item.glove.desc.4"),
                    EnumChatFormatting.WHITE + "["
                        + EnumChatFormatting.GOLD
                        + Keyboard.getKeyName(UtilitiesInExcess.proxy.GLOVE_KEYBIND.getKeyCode())
                        + EnumChatFormatting.WHITE
                        + "]"
                        + EnumChatFormatting.AQUA));
        } else tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("shift_for_description"));
    }

    public static boolean isUsingGlove(EntityPlayer player) {
        if (player == null) return false;

        return (player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof ItemGlove)
            || (UIEUtils.hasBauble(player, ItemGlove.class) && UtilitiesInExcess.proxy.GLOVE_KEYBIND.isKeyDown(player));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.UNIVERSAL;
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {

    }

    @Optional.Method(modid = "Baubles")
    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase entity) {

    }

    @Optional.Method(modid = "Baubles")
    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase entity) {

    }

    @Optional.Method(modid = "Baubles")
    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
