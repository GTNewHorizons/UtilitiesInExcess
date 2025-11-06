package com.fouristhenumber.utilitiesinexcess.utils;

import static com.fouristhenumber.utilitiesinexcess.common.items.ItemInvertedIngot.INVERTED_INGOT;
import static com.fouristhenumber.utilitiesinexcess.common.items.ItemInvertedIngot.checkImplosion;

import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemInvertedIngot;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class FMLEventHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.openContainer instanceof ContainerWorkbench bench) {
            ItemStack cursorItem = event.player.inventory.getItemStack();
            if (cursorItem != null && cursorItem.getItem() instanceof ItemInvertedIngot) {
                if (checkImplosion(cursorItem, event.player, event.player.worldObj)) {
                    event.player.inventory.setItemStack(null);
                    event.player.closeScreen();
                    event.player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
                }
            }
            for (int i = 0; i < bench.craftMatrix.getSizeInventory(); i++) {
                ItemStack stack = bench.craftMatrix.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemInvertedIngot) {
                    if (checkImplosion(stack, event.player, event.player.worldObj)) {
                        bench.craftMatrix.setInventorySlotContents(i, null);
                        event.player.closeScreen();
                        event.player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
                    }
                }
            }
        }
    }
}
