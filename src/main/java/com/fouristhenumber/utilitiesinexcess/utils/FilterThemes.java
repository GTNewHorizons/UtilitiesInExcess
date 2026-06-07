package com.fouristhenumber.utilitiesinexcess.utils;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.theme.SlotTheme;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;

public class FilterThemes
{
    public static WidgetThemeKey<SlotTheme> PINK_SLOT = IThemeApi.get().widgetThemeKeyBuilder("pinkSlot", SlotTheme.class)
        .defaultTheme(new SlotTheme(18, 18, GuiTextures.SLOT_ITEM, Color.PINK.main, 0xFF404040, false, Color.PINK.main, Color.withAlpha(Color.PINK.main, 0x60)))
        .register();
}
