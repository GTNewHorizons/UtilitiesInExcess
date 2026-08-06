package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.NotNull;

import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.ToolCore;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.weaponry.weapons.Crossbow;
import tconstruct.weaponry.weapons.LongBow;
import tconstruct.weaponry.weapons.ShortBow;

public class TinkersCompat {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Events());

        TConstructRegistry.registerActiveToolMod(new BedrockiumActiveToolMod());
        TinkersMaterials.registerMaterials();
    }

    @SuppressWarnings("unused")
    public static class Events {

        @SubscribeEvent
        public void craftTool(ToolCraftEvent.NormalTool event) {
            NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");

            List<String> componentNames = getComponentNames(event.tool);

            for (String component : componentNames) {
                if (toolTag.getInteger(component) == OtherConfig.bedrockiumTinkersID) {
                    toolTag.setBoolean("Heavy", true);
                    break;
                }
            }

            boolean allInverted = true;
            for (String component : componentNames) {
                if (toolTag.hasKey(component) && toolTag.getInteger(component) != OtherConfig.invertedTinkersID) {
                    allInverted = false;
                    break;
                }
            }
            if (allInverted) toolTag.setInteger("Unbreaking", 10);

            boolean allMagicWood = true;
            int bonusMods = 0;
            for (String component : componentNames) {
                if (toolTag.hasKey(component)) {
                    if (toolTag.getInteger(component) != OtherConfig.magicalWoodTinkersID) {
                        allMagicWood = false;
                    } else bonusMods++;
                }
            }
            if (allMagicWood) {
                toolTag.setInteger("Modifiers", toolTag.getInteger("Modifiers") + 8);
                toolTag.setBoolean("MagicallyModifiable", true);
            } else {
                toolTag.setInteger("Modifiers", toolTag.getInteger("Modifiers") + bonusMods);
            }
        }
    }

    // This hardcoded nonsense accounts for the way tools work in ticon. We need to not check the Accessory
    // or Handle for arrows, because those can never be made from our materials. We need to not check Accessory
    // for Crossbows (that's the bowstring) for the same reason, and Handle for Long and Short Bows (their
    // bowstrings). Lastly, we have to not check the Head or Accessory for Crossbow Bolts. Tinker's API doesn't expose
    // the information needed to do this more cleanly.
    private static @NotNull List<String> getComponentNames(ToolCore tool) {
        List<String> componentNames = new ArrayList<>();

        componentNames.add("Extra");
        if (!(tool instanceof BoltAmmo)) {
            componentNames.add("Head");
        }
        if (!(tool instanceof ArrowAmmo)) {
            if (!(tool instanceof Crossbow) && !(tool instanceof BoltAmmo)) {
                componentNames.add("Accessory");
            }
            if (!(tool instanceof LongBow) && !(tool instanceof ShortBow)) {
                componentNames.add("Handle");
            }
        }
        return componentNames;
    }
}
