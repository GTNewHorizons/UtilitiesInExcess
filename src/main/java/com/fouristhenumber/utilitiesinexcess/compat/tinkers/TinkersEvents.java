package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import static com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersMaterials.bedrockiumID;
import static com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersMaterials.invertedID;
import static com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersMaterials.magicalWoodID;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
}
