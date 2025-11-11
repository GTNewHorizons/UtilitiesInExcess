package com.fouristhenumber.utilitiesinexcess.common.items;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig.awakenedInversionDurability;

public class ItemPseudoInversionSigil extends Item {

    public ItemPseudoInversionSigil() {
        super();
        setUnlocalizedName("pseudo_inversion_sigil");
        setTextureName("utilitiesinexcess:pseudo_inversion_sigil");
        setMaxStackSize(1);
        setContainerItem(this);
    }

    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List<ItemStack> p_150895_3_) {
        p_150895_3_.add(getStack());
    }

    public static @NotNull ItemStack getStack() {
        ItemStack stack = new ItemStack(ModItems.PSEUDO_INVERSION_SIGIL.get(), 1);

        NBTTagCompound tag = new NBTTagCompound();
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tt, boolean p_77624_4_) {
        NBTTagCompound tag = stack.getTagCompound();
        super.addInformation(stack, player, tt, p_77624_4_);
    }
}
