package com.fouristhenumber.utilitiesinexcess.compat.findit;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnh.findit.fx.EntityHighlighter;
import com.gtnh.findit.util.ClientFinderHelperUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@SuppressWarnings("unused")
@EventBusSubscriber(side = Side.CLIENT)
public class FindItHelper {

    public static EntityHighlighter entityHighlighter;

    public static FindItHelper INSTANCE;

    public static void init() {
        entityHighlighter = new EntityHighlighter();
    }

    public static void rotateViewHelper(List<ChunkPosition> targets) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        player.closeScreen();
        ClientFinderHelperUtils.rotateViewHelper(player, targets);
    }

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return Mods.FindIt.isLoaded();
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        FindItHelper.entityHighlighter.renderHighlightedEntities(event);
    }
}
