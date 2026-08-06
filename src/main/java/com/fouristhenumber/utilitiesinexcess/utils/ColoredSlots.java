package com.fouristhenumber.utilitiesinexcess.utils;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.theme.SlotTheme;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;

public enum ColoredSlots
{
    PINK_SLOT(0xee90ad),
    ORANGE_SLOT(0xf17716),
    PURPLE_SLOT(0x4c17a9),
    GREEN_SLOT(0x556e1c),
    CYAN_SLOT(0x3cb0da),
    YELLOW_SLOT(0xf1af15);

    public static void init() {};


    public WidgetThemeKey<SlotTheme> SLOT_KEY;

    ColoredSlots(int color)
    {
        SLOT_KEY = IThemeApi.get().widgetThemeKeyBuilder(this.toString(), SlotTheme.class)
            .defaultTheme(new SlotTheme(18, 18, GuiTextures.SLOT_ITEM, color, 0xFF404040, false, color, Color.withAlpha(color, 0x60)))
            .register();
    }

    public WidgetThemeKey<SlotTheme> get()
    {
        return SLOT_KEY;
    }
}
