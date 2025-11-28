package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;

public class VillagerSyncHandler extends SyncHandler {

    public static final String FAVORITE_TRADES_SUBTAG = "Favorite_Trades";

    private final VillagerWidget column;
    private final PosGuiData data;
    private MerchantRecipeListWrapper recipeList;

    public VillagerSyncHandler(VillagerWidget column, PosGuiData data, MerchantRecipeListWrapper recipeList) {
        super();
        this.column = column;
        this.data = data;
        this.recipeList = recipeList;

        this.recipeList.setFavorites(getFavorites());
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

    public void tradeClick(int mouseButton, int index, boolean shift, boolean alt) {
        syncToServer(1, buffer -> {
            buffer.writeInt(mouseButton);
            buffer.writeInt(index);
            buffer.writeBoolean(shift);
            buffer.writeBoolean(alt);
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
        int mouseButton = buf.readInt();
        int index = buf.readInt();
        boolean shift = buf.readBoolean();
        boolean alt = buf.readBoolean();

        if (!alt && mouseButton == 0) executeTrade(index, shift);
        else if (alt && mouseButton == 0) toggleFavorite(index);

    }

    private NBTTagCompound getFavoritesTag() {
        EntityPlayer player = data.getPlayer();
        NBTTagCompound uieTag = UIEUtils.getUIETag(player);
        NBTTagCompound allFavoritesTag;

        if (uieTag.hasKey(FAVORITE_TRADES_SUBTAG)) {
            allFavoritesTag = uieTag.getCompoundTag(FAVORITE_TRADES_SUBTAG);
        } else {
            allFavoritesTag = new NBTTagCompound();
            uieTag.setTag(FAVORITE_TRADES_SUBTAG, allFavoritesTag);
        }
        return allFavoritesTag;
    }

    private int[] getFavorites() {
        String merchantID;
        if (recipeList.getMerchant() instanceof Entity) {
            merchantID = ((Entity) recipeList.getMerchant()).getUniqueID()
                .toString();
        } else {
            return null;
        }

        NBTTagCompound allFavoritesTag = getFavoritesTag();

        int[] favoritesTag;
        if (allFavoritesTag.hasKey(merchantID)) {
            favoritesTag = allFavoritesTag.getIntArray(merchantID);
        } else {
            favoritesTag = null;
        }
        return favoritesTag;
    }

    private void setFavorites(int[] favorites) {
        String merchantID;
        if (recipeList.getMerchant() instanceof Entity) {
            merchantID = ((Entity) recipeList.getMerchant()).getUniqueID()
                .toString();
        } else {
            return;
        }

        NBTTagCompound allFavoritesTag = getFavoritesTag();

        allFavoritesTag.setIntArray(merchantID, favorites);
    }

    public void addFavorite(int index) {
        int[] favorites = getFavorites();
        if (favorites == null) {
            favorites = new int[0];
        }
        favorites = Arrays.copyOf(favorites, favorites.length + 1);
        favorites[favorites.length - 1] = index;

        setFavorites(favorites);
    }

    public void removeFavorite(int index) {
        int[] favorites = getFavorites();
        if (favorites == null) {
            return;
        }

        favorites = Arrays.stream(favorites)
            .filter(x -> x != index)
            .toArray();

        setFavorites(favorites);
    }

    public boolean isFavorite(int index) {
        int[] favorites = getFavorites();
        if (favorites == null) {
            return false;
        }

        for (int x : favorites) {
            if (x == index) return true;
        }

        return false;
    }

    public void toggleFavorite(int index) {
        if (isFavorite(index)) removeFavorite(index);
        else addFavorite(index);
    }

    public boolean executeTrade(int index, boolean shift) {
        IMerchant merchant = recipeList.getMerchant();
        if (merchant == null || (merchant instanceof EntityLiving && !((EntityLiving) merchant).isEntityAlive()))
            return false;

        EntityPlayer player = data.getPlayer();
        MerchantRecipe recipe = (MerchantRecipe) recipeList.getRecipeList()
            .get(index);
        ItemStack price1 = recipe.getItemToBuy();
        ItemStack price2 = recipe.getSecondItemToBuy();
        ItemStack result = recipe.getItemToSell();

        // Count how many of each price item we have
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
            } else if (ItemStack.areItemStackTagsEqual(itemStack, price2) && price2 != null
                && itemStack.isItemEqual(price2)) {
                    count2 += itemStack.stackSize;
                    price2slots.add(i);
                }
        }

        // How many times can we pay the first time
        int a = count1 / price1.stackSize;
        // How many times can we pay the second item
        int b = price2 != null ? count2 / price2.stackSize : Integer.MAX_VALUE;
        // How many can we buy while still taking up one inventory slot
        int c = result.getItem()
            .getItemStackLimit(result) / result.stackSize;
        // How many uses does the recipe have left
        int d = ((AccessorMerchantRecipe) recipe).getMaxUses() - (((AccessorMerchantRecipe) recipe).getCurrentUses());
        int count = Math.min(Math.min(Math.min(a, b), shift ? c : 1), d);

        if (count < 1) return false;

        try {
            merchant.setCustomer(player);
            for (int i = 0; i < count; i++) {
                merchant.useRecipe(recipe);
            }
            merchant.setCustomer(null);
        } catch (Throwable e) {
            return false;
        }

        ItemStack resultStack = result.copy();
        resultStack.stackSize = result.stackSize * count;
        if (!player.inventory.addItemStackToInventory(resultStack)) return false;

        // Remove cost from player inventory
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

    public MerchantRecipeListWrapper getRecipeList() {
        return recipeList;
    }

}
