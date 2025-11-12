package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig.awakenedInversionDurability;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

public class ItemInversionSigilActive extends Item {

    private static final String DURABILITY_NBT_KEY = "RemainingUses";
    private static final int BEACON_SEARCH_RADIUS = 6;

    private boolean checkChest(TileEntityChest chest, ItemStack[] CHECKED_ITEMS, int ITEM_REQUIREMENT,
        int CHECKED_ITEMS_SIZE) {
        int requiredItemsAmount = 0;
        boolean[] hasItem = new boolean[20];
        for (int i = 0; i < chest.getSizeInventory(); i++) {
            ItemStack stack = chest.getStackInSlot(i);
            for (int j = 0; j < CHECKED_ITEMS_SIZE; j++) {
                if (stack != null && ItemStack.areItemStacksEqual(stack, CHECKED_ITEMS[j])) {
                    hasItem[j] = true;
                    break;
                }
            }
        }
        for (int i = 0; i < CHECKED_ITEMS_SIZE; i++) {
            if (hasItem[i]) {
                requiredItemsAmount++;
            }
        }
        return requiredItemsAmount >= ITEM_REQUIREMENT;
    }

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

        boolean dimensionOk = (world.provider.dimensionId == 1);
        boolean chestNorthExistsOk = (world.getBlock(x, y, z - 4) == Blocks.chest);
        boolean chestEastExistsOk = (world.getBlock(x + 4, y, z) == Blocks.chest);
        boolean chestSouthExistsOk = (world.getBlock(x, y, z + 4) == Blocks.chest);
        boolean chestWestExistsOk = (world.getBlock(x - 4, y, z) == Blocks.chest);
        boolean chestNorthContentsOk;
        boolean chestEastContentsOk;
        boolean chestSouthContentsOk;
        boolean chestWestContentsOk;

        ItemStack[] CHEST_NORTH_CONTENTS = { new ItemStack(Blocks.stone), new ItemStack(Items.brick),
            new ItemStack(Blocks.glass), new ItemStack(Items.cooked_fished), new ItemStack(Blocks.hardened_clay),
            new ItemStack(Items.dye, 1, 2), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.cooked_beef),
            new ItemStack(Items.iron_ingot), new ItemStack(Items.cooked_chicken), new ItemStack(Items.gold_ingot),
            new ItemStack(Items.baked_potato), new ItemStack(Items.cooked_porkchop), new ItemStack(Items.netherbrick) };

        ItemStack[] CHEST_SOUTH_CONTENTS = { new ItemStack(Blocks.grass), new ItemStack(Blocks.lapis_ore),
            new ItemStack(Blocks.dirt), new ItemStack(Blocks.obsidian), new ItemStack(Blocks.sand),
            new ItemStack(Blocks.diamond_ore), new ItemStack(Blocks.gravel), new ItemStack(Blocks.redstone_ore),
            new ItemStack(Blocks.gold_ore), new ItemStack(Blocks.clay), new ItemStack(Blocks.iron_ore),
            new ItemStack(Blocks.emerald_ore), new ItemStack(Blocks.coal_ore) };

        if (world.getTileEntity(x, y, z - 4) instanceof TileEntityChest chest) {
            chestNorthContentsOk = checkChest(chest, CHEST_NORTH_CONTENTS, InversionConfig.northChestRequiredItems, 14);
        } else {
            chestNorthContentsOk = false;
        }
        if (world.getTileEntity(x, y, z + 4) instanceof TileEntityChest chest) {
            chestSouthContentsOk = checkChest(chest, CHEST_SOUTH_CONTENTS, InversionConfig.southChestRequiredItems, 13);
        } else {
            chestSouthContentsOk = false;
        }
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
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestNorthExists",
                    (chestNorthExistsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestEastExists",
                    (chestEastExistsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestSouthExists",
                    (chestSouthExistsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestWestExists",
                    (chestWestExistsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestNorthContents",
                    (chestNorthContentsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestSouthContents",
                    (chestSouthContentsOk ? "✓" : "✗"))));
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
