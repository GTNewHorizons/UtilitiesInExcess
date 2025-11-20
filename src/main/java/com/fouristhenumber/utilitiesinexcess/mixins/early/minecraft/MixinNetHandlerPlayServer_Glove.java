package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fouristhenumber.utilitiesinexcess.ClientProxy;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemGlove;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_Glove {

    @Unique
    boolean uie$isUsingGlove;
    @Unique
    ItemStack uie$theGlove;

    @Inject(method = "processPlayerBlockPlacement", at = @At("HEAD"), remap = false)
    private void uie$UseItemEarly(C08PacketPlayerBlockPlacement packetIn, CallbackInfo ci) {
        NetHandlerPlayServer thisObject = (NetHandlerPlayServer) (Object) this;
        InventoryPlayer inventory = thisObject.playerEntity.inventory;
        ItemStack itemstack = inventory.getCurrentItem();
        if ((itemstack != null && itemstack.getItem() instanceof ItemGlove)
            || (UIEUtils.hasBauble(thisObject.playerEntity, ItemGlove.class)
                && ClientProxy.gloveKey.getIsKeyPressed())) {
            uie$isUsingGlove = true;
            uie$theGlove = itemstack;
            inventory.mainInventory[inventory.currentItem] = null;
        }
    }

    @Inject(method = "processPlayerBlockPlacement", at = @At("TAIL"), remap = false)
    private void uie$UseItemLate(C08PacketPlayerBlockPlacement packetIn, CallbackInfo ci) {
        NetHandlerPlayServer thisObject = (NetHandlerPlayServer) (Object) this;
        InventoryPlayer inventory = thisObject.playerEntity.inventory;
        EntityPlayer player = thisObject.playerEntity;

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
        }
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true, remap = false)
    private void uie$cancelItemChange(Packet packetIn, CallbackInfo ci) {
        if (packetIn instanceof S2FPacketSetSlot && uie$isUsingGlove) {
            ci.cancel();
        }
    }
}
