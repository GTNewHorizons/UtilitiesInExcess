package com.fouristhenumber.utilitiesinexcess.compat.findit;

import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.gtnh.findit.fx.EntityHighlighter;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FindItHelper {

    public static EntityHighlighter entityHighlighter;

    public static FindItHelper INSTANCE;

    public static void init() {
        entityHighlighter = new EntityHighlighter();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        FindItHelper.entityHighlighter.renderHighlightedEntities(event);
    }
}
