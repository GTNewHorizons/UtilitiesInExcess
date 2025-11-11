package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig.awakenedInversionDurability;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.fouristhenumber.utilitiesinexcess.ModItems;

public class ItemInversionSigilActive extends Item {

    private static final String DURABILITY_NBT_KEY = "RemainingUses";
    private static final int BEACON_SEARCH_RADIUS = 6;

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            return false;
        }

        Block clicked = world.getBlock(x, y, z);
        if (clicked != Blocks.beacon) {
            return false;
        }

        if (world.isRemote) return true;

        boolean dimensionOk;

        dimensionOk = (world.provider.dimensionId == 1);

        player.addChatMessage(
            new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.header")));
        if (dimensionOk) {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.dimension")));
        } else if (world.provider.dimensionId == 0) {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.overworld")));
        } else if (world.provider.dimensionId == -1) {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.nether")));
        } else {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.misc")));
        }
        return true;
    }

    public ItemInversionSigilActive() {
        super();
        setUnlocalizedName("inversion_sigil_active");
        setTextureName("utilitiesinexcess:inversion_sigil_active");
        setMaxStackSize(1);
        setContainerItem(this);
    }

    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List<ItemStack> p_150895_3_) {
        p_150895_3_.add(getStack());
    }

    public static @NotNull ItemStack getStack() {
        ItemStack stack = new ItemStack(ModItems.INVERSION_SIGIL_ACTIVE.get(), 1);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(DURABILITY_NBT_KEY, awakenedInversionDurability);
        stack.setTagCompound(tag);
        return stack;
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
