package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;

public class KeybindUtils {

    public static String getKeyDisplayNameWithMouse(KeyBinding keyBinding) {
        return switch (keyBinding.getKeyCode()) {
            case -100 -> StatCollector.translateToLocal("uie.key.lclick");
            case -99 -> StatCollector.translateToLocal("uie.key.rclick");
            case -98 -> StatCollector.translateToLocal("uie.key.mclick");
            default -> getKeyDisplayName(keyBinding);
        };
    }

    private static String getKeyDisplayName(KeyBinding keyBinding) {
        // TODO: Uncomment once https://github.com/GTNewHorizons/Controlling/pull/22 is merged
        // if (Mods.Controlling.isLoaded()) {
        // return ControllingApi.getDisplayName(keyBinding);
        // }
        return GameSettings.getKeyDisplayString(keyBinding.getKeyCode());
    }
}
