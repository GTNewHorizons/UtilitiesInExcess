package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost;

@Mixin(MerchantRecipe.class)
public class MixinMerchantRecipe implements TileEntityTradingPost.IMerchantRecipeExtension {

    @Unique
    public boolean uie$favorite;
    @Unique
    public EntityPlayer uie$player;
    // Let's assume the merchant is an entity, which tbh it should almost always be
    @Unique
    public IMerchant uie$merchant;
    @Unique
    public int uie$index;

    @Inject(method = "readFromTags", at = @At(value = "TAIL"))
    public void uie$readFromTags(NBTTagCompound compound, CallbackInfo ci) {
        World world = Minecraft.getMinecraft().theWorld;
        String playerId = compound.getString("player");
        uie$setFavorite(compound.getBoolean("favorite"));
        for (EntityPlayer playerEntity : world.playerEntities) {
            if (playerEntity.getGameProfile()
                .getId() == UUID.fromString(playerId)) {
                uie$setPlayer(playerEntity);
                break;
            }
        }
        uie$setMerchant((IMerchant) world.getEntityByID(compound.getInteger("merchant")));
        uie$setListIndex(compound.getInteger("index"));
    }

    @Inject(method = "writeToTags", at = @At(value = "TAIL"))
    public void uie$writeToTags(CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound compound = cir.getReturnValue();
        compound.setBoolean("favorite", this.uie$getFavorite());
        compound.setString(
            "player",
            this.uie$getPlayer()
                .getGameProfile()
                .getId()
                .toString());
        compound.setInteger("merchant", ((Entity) this.uie$getMerchant()).getEntityId());
        compound.setInteger("index", this.uie$getListIndex());
    }

    @Override
    public boolean uie$getFavorite() {
        return uie$favorite;
    }

    @Override
    public void uie$setFavorite(boolean value) {
        uie$favorite = value;
    }

    @Override
    public EntityPlayer uie$getPlayer() {
        return uie$player;
    }

    @Override
    public void uie$setPlayer(EntityPlayer player) {
        uie$player = player;
    }

    @Override
    public IMerchant uie$getMerchant() {
        return uie$merchant;
    }

    @Override
    public void uie$setMerchant(IMerchant merchant) {
        uie$merchant = merchant;
    }

    @Override
    public int uie$getListIndex() {
        return uie$index;
    }

    @Override
    public void uie$setListIndex(int index) {
        uie$index = index;
    }

}
