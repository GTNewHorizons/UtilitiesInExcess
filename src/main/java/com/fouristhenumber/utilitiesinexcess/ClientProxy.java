package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fouristhenumber.utilitiesinexcess.common.renderers.GloveRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.InvertedIngotRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.fouristhenumber.utilitiesinexcess.render.TESRUnderworldPortal;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientProxy extends CommonProxy {

    // This is just a number that ticks up every frame.
    public static int frameCount = 0;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        if (ModItems.INVERTED_NUGGET.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.INVERTED_INGOT.get(), new InvertedIngotRenderer());
        }
        if (ModBlocks.UNDERWORLD_PORTAL.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalUnderWorld.class, new TESRUnderworldPortal());
            RenderingRegistry.registerBlockHandler(ISBRHUnderworldPortal.INSTANCE);
        }
        if (ModItems.GLOVE.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.GLOVE.get(), new GloveRenderer());
        }

        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @SubscribeEvent
    public void tickRender(TickEvent.RenderTickEvent event) {
        frameCount++;
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if (!(mc.currentScreen == null && mc.theWorld != null
                && Minecraft.isGuiEnabled()
                && !mc.gameSettings.keyBindPlayerList.getIsKeyPressed())) return;

            GL11.glPushMatrix();

            boolean hasBlending = GL11.glGetBoolean(GL11.GL_BLEND);
            boolean hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
            int boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            GL11.glPushAttrib(GL11.GL_CURRENT_BIT);

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GloveRenderer.renderGloveHudIcon();

            if (hasBlending) GL11.glEnable(GL11.GL_BLEND);
            else GL11.glDisable(GL11.GL_BLEND);
            if (hasDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
            else GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
