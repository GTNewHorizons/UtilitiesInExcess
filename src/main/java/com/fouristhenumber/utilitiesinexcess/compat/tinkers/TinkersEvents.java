package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;

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
            if (toolTag.getInteger(component) == OtherConfig.bedrockiumTinkersID) {
                toolTag.setBoolean("Heavy", true);
                break;
            }
        }

        boolean allInverted = true;
        for (String component : components) {
            if (toolTag.hasKey(component) && toolTag.getInteger(component) != OtherConfig.invertedTinkersID) {
                allInverted = false;
                break;
            }
        }
        if (allInverted) toolTag.setInteger("Unbreaking", 10);

        boolean allMagicWood = true;
        for (String component : components) {
            if (toolTag.hasKey(component) && toolTag.getInteger(component) != OtherConfig.magicalWoodTinkersID) {
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
