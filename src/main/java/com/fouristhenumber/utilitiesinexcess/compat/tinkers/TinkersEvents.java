package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import static com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersMaterials.bedrockiumID;
import static com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersMaterials.invertedID;
import static com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersMaterials.magicalWoodID;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import tconstruct.library.event.ToolCraftEvent;

public class TinkersEvents {

    @SubscribeEvent
    public void craftTool(ToolCraftEvent.NormalTool event) {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");

        ArrayList<String> components = new ArrayList<>();
        components.add("Head");
        components.add("Handle");
        components.add("Accessory");
        components.add("Extra");

        for (String component : components) {
            if (toolTag.getInteger(component) == bedrockiumID) {
                toolTag.setBoolean("Heavy", true);
                break;
            }
        }

        boolean allInverted = true;
        for (String component : components) {
            if (toolTag.hasKey(component) && toolTag.getInteger(component) != invertedID) {
                allInverted = false;
                break;
            }
        }
        if (allInverted) toolTag.setInteger("Unbreaking", 10);

        boolean allMagicWood = true;
        for (String component : components) {
            if (toolTag.hasKey(component) && toolTag.getInteger(component) != magicalWoodID) {
                allMagicWood = false;
                break;
            }
        }
        if (allMagicWood) {
            toolTag.setInteger("Modifiers", toolTag.getInteger("Modifiers") + 8);
            toolTag.setBoolean("MagicallyModifiable", true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        ItemStack held = event.player.getHeldItem();
        if (held == null) return;
        NBTTagCompound tag = held.getTagCompound();
        if (tag == null || !tag.hasKey("InfiTool")) return;
        NBTTagCompound toolTag = tag.getCompoundTag("InfiTool");

        if (toolTag.getBoolean("Heavy")) {
            event.player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 0, 1, true));
        }
    }
}
