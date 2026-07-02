package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.StatCollector;

public class KeybindUtils {

    public static String getKeyDisplayNameWithMouse(int key) {
        return switch (key) {
            case -100 -> StatCollector.translateToLocal("uie.util.key.lclick");
            case -99 -> StatCollector.translateToLocal("uie.util.key.rclick");
            case -98 -> StatCollector.translateToLocal("uie.util.key.mclick");
            default -> GameSettings.getKeyDisplayString(key);
        };
    }
}
