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

public class MerchantRecipeListWrapper {

    private MerchantRecipeList recipeList;

    private int recipeListHash;

    // private final EntityPlayer player;

    private final IMerchant merchant;

    // private final int index;

    public MerchantRecipeListWrapper(IMerchant merchant, EntityPlayer player) {
        this.recipeList = merchant.getRecipes(player);
        updateRecipeListHash();
        // this.player = player;
        this.merchant = merchant;
        // this.index = index;
    }

    public MerchantRecipeListWrapper(NBTTagCompound tagCompound) throws IOException {
        recipeList = new MerchantRecipeList(tagCompound);

        World world = Minecraft.getMinecraft().theWorld;
        String playerId = tagCompound.getString("player");
        // index = tagCompound.getInteger("index");
        if (world == null) throw new IOException("Trying to create MerchantRecipeWrapper with null World");
        // if (playerId.isEmpty()) throw new IOException("Trying to create MerchantRecipeWrapper with null playerID");
        // EntityPlayer _player = null;
        // for (EntityPlayer playerEntity : world.playerEntities) {
        // if (playerEntity.getGameProfile()
        // .getId()
        // .toString()
        // .equals(playerId)) {
        // _player = playerEntity;
        // break;
        // }
        // }
        // if (_player == null) throw new IOException("Trying to create MerchantRecipeWrapper but couldn't find
        // player");
        // this.player = _player;
        merchant = (IMerchant) world.getEntityByID(tagCompound.getInteger("merchant"));
    }

    public void readFromTags(NBTTagCompound compound) {}

    public NBTTagCompound writeToTags() {
        NBTTagCompound compound = recipeList.getRecipiesAsTags();
        // if (this.getPlayer() != null) {
        // compound.setString(
        // "player",
        // this.getPlayer()
        // .getGameProfile()
        // .getId()
        // .toString());
        // }
        if (this.getMerchant() != null) compound.setInteger("merchant", ((Entity) this.getMerchant()).getEntityId());
        // compound.setInteger("index", this.getListIndex());
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

    // public EntityPlayer getPlayer() {
    // return player;
    // }

    public IMerchant getMerchant() {
        return merchant;
    }

    // public int getListIndex() {
    // return index;
    // }
}
