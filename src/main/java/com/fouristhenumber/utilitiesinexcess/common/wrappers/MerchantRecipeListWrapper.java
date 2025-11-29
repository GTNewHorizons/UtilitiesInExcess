package com.fouristhenumber.utilitiesinexcess.common.wrappers;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MerchantRecipeListWrapper {

    private MerchantRecipeList recipeList;
    private int recipeListHash;
    private final IMerchant merchant;
    private int[] favorites;

    public MerchantRecipeListWrapper(IMerchant merchant, EntityPlayer player) {
        this.recipeList = merchant.getRecipes(player);
        updateRecipeListHash();
        this.merchant = merchant;
    }

    @SideOnly(Side.CLIENT)
    public MerchantRecipeListWrapper(NBTTagCompound tagCompound) throws IOException {
        recipeList = new MerchantRecipeList(tagCompound);
        World world = Minecraft.getMinecraft().theWorld;
        merchant = (IMerchant) world.getEntityByID(tagCompound.getInteger("merchant"));
        favorites = tagCompound.getIntArray("favorites");
    }

    public NBTTagCompound writeToTags() {
        NBTTagCompound compound = recipeList.getRecipiesAsTags();
        if (this.getMerchant() != null) compound.setInteger("merchant", ((Entity) this.getMerchant()).getEntityId());
        if (this.getFavorites() != null) compound.setIntArray("favorites", this.getFavorites());
        return compound;
    }

    public MerchantRecipeList getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(MerchantRecipeList recipeList) {
        this.recipeList = recipeList;
    }

    public int getRecipeListHash() {
        return recipeListHash;
    }

    public void updateRecipeListHash() {
        this.recipeListHash = getCustomHashFromList(this.recipeList);
    }

    public static int getCustomHashFromList(MerchantRecipeList recipeList) {
        ArrayList<Integer> useCounts = new ArrayList<>();
        for (Object recipe : recipeList) {
            if (recipe instanceof MerchantRecipe) {
                useCounts.add(((AccessorMerchantRecipe) recipe).getCurrentUses());
                useCounts.add(((AccessorMerchantRecipe) recipe).getMaxUses());
            }
        }
        return recipeList.hashCode() + useCounts.hashCode();
    }

    public IMerchant getMerchant() {
        return merchant;
    }

    public int[] getFavorites() {
        return favorites;
    }

    public void setFavorites(int[] favorites) {
        this.favorites = favorites;
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
}
