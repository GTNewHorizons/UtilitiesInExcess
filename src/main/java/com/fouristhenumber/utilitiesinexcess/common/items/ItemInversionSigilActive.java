package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig.awakenedInversionDurability;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.fouristhenumber.utilitiesinexcess.ModItems;

public class ItemInversionSigilActive extends Item {

    private static final String DURABILITY_NBT_KEY = "RemainingUses";

    public ItemInversionSigilActive() {
        super();
        setUnlocalizedName("inversion_sigil_active");
        setTextureName("utilitiesinexcess:inversion_sigil_active");
        setMaxStackSize(1);
        setContainerItem(this);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        if (awakenedInversionDurability == 0) return itemStack;

        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) return null;

        int uses = tag.getInteger(DURABILITY_NBT_KEY);
        if (uses == 1) return new ItemStack(ModItems.INVERSION_SIGIL_INACTIVE.get(), 1);

        tag.setInteger(DURABILITY_NBT_KEY, uses - 1);

        return itemStack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tt, boolean p_77624_4_) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && awakenedInversionDurability != 0) {
            tt.add(
                StatCollector
                    .translateToLocalFormatted("item.inversion_sigil_active.desc", tag.getInteger(DURABILITY_NBT_KEY)));
        }
        super.addInformation(stack, player, tt, p_77624_4_);
    }
}
