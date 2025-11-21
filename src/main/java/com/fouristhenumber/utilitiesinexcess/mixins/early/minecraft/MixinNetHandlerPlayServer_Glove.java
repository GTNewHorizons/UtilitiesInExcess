package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_Glove {

    @Unique
    boolean uie$isUsingGlove = false;
    @Unique
    ItemStack uie$theGlove;

    @Inject(method = "processPlayerBlockPlacement", at = @At("HEAD"))
    private void uie$UseItemEarly(C08PacketPlayerBlockPlacement packetIn, CallbackInfo ci) {
        NetHandlerPlayServer thisObject = (NetHandlerPlayServer) (Object) this;
        EntityPlayer player = thisObject.playerEntity;
        InventoryPlayer inventory = player.inventory;
        ItemStack itemstack = inventory.getCurrentItem();
        ContainerPlayer inventoryContainer = (ContainerPlayer) player.inventoryContainer;

        if (((itemstack != null && itemstack.getItem() instanceof ItemGlove)
            || UIEUtils.hasBauble(player, ItemGlove.class))
            && UtilitiesInExcess.proxy.GLOVE_KEYBIND.isKeyDown(player)) {
            uie$isUsingGlove = true;
            if (uie$theGlove != null) {
                EntityItem entityitem = new EntityItem(
                    player.worldObj,
                    player.posX,
                    player.posY + 0.5,
                    player.posZ,
                    uie$theGlove);
                entityitem.delayBeforeCanPickup = 5;
                player.worldObj.spawnEntityInWorld(entityitem);
            }

            uie$theGlove = itemstack;
            inventory.mainInventory[inventory.currentItem] = null;
            if (inventoryContainer.inventoryItemStacks.size() > 36 + inventory.currentItem)
                inventoryContainer.inventoryItemStacks.set(36 + inventory.currentItem, null);
        }
    }

    @Inject(method = "processPlayerBlockPlacement", at = @At("RETURN"))
    private void uie$UseItemLate(C08PacketPlayerBlockPlacement packetIn, CallbackInfo ci) {
        NetHandlerPlayServer thisObject = (NetHandlerPlayServer) (Object) this;
        InventoryPlayer inventory = thisObject.playerEntity.inventory;
        EntityPlayer player = thisObject.playerEntity;
        ContainerPlayer inventoryContainer = (ContainerPlayer) player.inventoryContainer;

        if (uie$isUsingGlove) {
            uie$isUsingGlove = false;
            if (inventory.mainInventory[inventory.currentItem] != null) {
                EntityItem entityitem = new EntityItem(
                    player.worldObj,
                    player.posX,
                    player.posY + 0.5,
                    player.posZ,
                    inventory.mainInventory[inventory.currentItem]);
                entityitem.delayBeforeCanPickup = 5;
                player.worldObj.spawnEntityInWorld(entityitem);
            }

            inventory.mainInventory[inventory.currentItem] = uie$theGlove;
            if (inventoryContainer.inventoryItemStacks.size() > 36 + inventory.currentItem)
                inventoryContainer.inventoryItemStacks.set(36 + inventory.currentItem, uie$theGlove);
            uie$theGlove = null;
        }
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void uie$cancelItemChange(Packet packetIn, CallbackInfo ci) {
        if (packetIn instanceof S2FPacketSetSlot && uie$isUsingGlove) {
            ci.cancel();
        }
    }

    @Definition(id = "itemstack", local = @Local(type = ItemStack.class))
    @Expression("itemstack == null")
    @WrapOperation(
        method = "processPlayerBlockPlacement",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;",
                ordinal = 1),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxItemUseDuration()I")))
    private boolean uie$isStackNull(Object left, Object right, Operation<Boolean> original) {
        if (uie$isUsingGlove) return false;

        return original.call(left, right);
    }

    @WrapOperation(
        method = "processPlayerBlockPlacement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxItemUseDuration()I"))
    private int uie$getMaxItemUseDuration(ItemStack instance, Operation<Integer> original) {
        if (uie$isUsingGlove) return 1;

        return original.call(instance);
    }
}
