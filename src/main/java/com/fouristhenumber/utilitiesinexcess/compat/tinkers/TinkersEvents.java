package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.event.ToolCraftEvent;

public class TinkersEvents {

    private static final String[] components = new String[] { "Head", "Handle", "Accessory", "Extra" };

    @SubscribeEvent
    public void craftTool(ToolCraftEvent.NormalTool event) {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");

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
