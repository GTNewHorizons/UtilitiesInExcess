package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.fouristhenumber.utilitiesinexcess.common.wrappers.MerchantRecipeListWrapper;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;

public class VillagerSyncHandler extends SyncHandler {

    private final VillagerWidget column;
    private final PosGuiData data;
    private MerchantRecipeListWrapper recipeList;

    public VillagerSyncHandler(VillagerWidget column, PosGuiData data, MerchantRecipeListWrapper recipeList) {
        super();
        this.column = column;
        this.data = data;
        this.recipeList = recipeList;
    }

    @Override
    public void detectAndSendChanges(boolean init) {
        if (data.isClient()) return;

        if (!init) {
            MerchantRecipeList actualList = recipeList.getMerchant()
                .getRecipes(data.getPlayer());
            if (recipeList.getRecipeListHash() == MerchantRecipeListWrapper.getCustomHashFromList(actualList)) return;

            recipeList.updateRecipeListHash();
        }

        syncToClient(0, buffer -> { buffer.writeNBTTagCompoundToBuffer(recipeList.writeToTags()); });
        column.syncRecipes(recipeList);
    }

    public void tradeClick(int index, boolean shift) {
        syncToServer(1, buffer -> {
            buffer.writeInt(index);
            buffer.writeBoolean(shift);
        });
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) throws IOException {
        if (id == 0) {
            NBTTagCompound recipeTag = buf.readNBTTagCompoundFromBuffer();
            try {
                MerchantRecipeListWrapper newRecipeList = new MerchantRecipeListWrapper(recipeTag);
                column.syncRecipes(newRecipeList);
                this.recipeList = newRecipeList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        int index = buf.readInt();
        boolean shift = buf.readBoolean();
        executeTrade(index, shift);
    }

    public boolean executeTrade(int index, boolean shift) {
        EntityPlayer player = data.getPlayer();
        MerchantRecipe recipe = (MerchantRecipe) recipeList.getRecipeList().get(index);
        ItemStack price1 = recipe.getItemToBuy();
        ItemStack price2 = recipe.getSecondItemToBuy();
        ItemStack result = recipe.getItemToSell();

        int count1 = 0;
        ArrayList<Integer> price1slots = new ArrayList<>();
        int count2 = 0;
        ArrayList<Integer> price2slots = new ArrayList<>();
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack itemStack = player.inventory.mainInventory[i];
            if (itemStack == null) continue;
            if (ItemStack.areItemStackTagsEqual(itemStack, price1) && itemStack.isItemEqual(price1)) {
                count1 += itemStack.stackSize;
                price1slots.add(i);
            } else if (ItemStack.areItemStackTagsEqual(itemStack, price2) && price2 != null && itemStack.isItemEqual(price2)) {
                count2 += itemStack.stackSize;
                price2slots.add(i);
            }
        }

        int a = count1 / price1.stackSize;
        int b = price2 != null ? count2 / price2.stackSize : Integer.MAX_VALUE;
        int c = result.getItem().getItemStackLimit(result) / result.stackSize;
        int d = ((AccessorMerchantRecipe) recipe).getMaxUses()
            - (((AccessorMerchantRecipe) recipe).getCurrentUses());
        int count = Math.min(Math.min(Math.min(a, b), shift ? c : 1), d);

        if (count < 1) return false;

        try {
            IMerchant merchant = recipeList.getMerchant();
            merchant.setCustomer(player);
            for (int i = 0; i < count; i++) {
                merchant.useRecipe(recipe);
            }
            merchant.setCustomer(null);
        } catch (NullPointerException e) {
            return false;
        }

        ItemStack resultStack = result.copy();
        resultStack.stackSize = result.stackSize * count;
        if (!player.inventory.addItemStackToInventory(resultStack)) return false;

        count1 = price1.stackSize * count;
        for (int i : price1slots) {
            ItemStack itemStack = player.inventory.mainInventory[i];
            if (itemStack.stackSize <= count1) {
                player.inventory.mainInventory[i] = null;
            } else {
                itemStack.stackSize -= count1;
            }
            count1 -= itemStack.stackSize;

            if (count1 <= 0) break;
        }
        if (price2 != null) {
            count2 = price2.stackSize * count;
            for (int i : price2slots) {
                ItemStack itemStack = player.inventory.mainInventory[i];
                if (itemStack.stackSize <= count2) {
                    player.inventory.mainInventory[i] = null;
                } else {
                    itemStack.stackSize -= count2;
                }
                count2 -= itemStack.stackSize;

                if (count2 <= 0) break;
            }
        }

        return true;
    }


}
