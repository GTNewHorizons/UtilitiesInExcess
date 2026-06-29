package com.fouristhenumber.utilitiesinexcess;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.MODID;

import net.minecraftforge.client.MinecraftForgeClient;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockTrueGreenscreen;
import com.fouristhenumber.utilitiesinexcess.common.renderers.ChunchunmaruRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.FireBatteryRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.GloveRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.InvertedIngotRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.FMPItems;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.item.ItemUEMultiPartRenderer;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.findit.FindItHelper;
import com.fouristhenumber.utilitiesinexcess.render.CollectorRangeBox;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.fouristhenumber.utilitiesinexcess.render.TESRTrueGreenscreen;
import com.fouristhenumber.utilitiesinexcess.render.TESRUnderworldPortal;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelRegistry.registerModid(MODID);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        if (ModItems.INVERTED_NUGGET.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.INVERTED_INGOT.get(), new InvertedIngotRenderer());
        }
        if (Mods.ForgeMicroBlock.isLoaded()) {
            MinecraftForgeClient.registerItemRenderer(FMPItems.UE_MULTI_PART.get(), new ItemUEMultiPartRenderer());
        }
        if (ModBlocks.UNDERWORLD_PORTAL.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalUnderWorld.class, new TESRUnderworldPortal());
            RenderingRegistry.registerBlockHandler(ISBRHUnderworldPortal.INSTANCE);
        }
        if (ModBlocks.COLLECTOR.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCollector.class, new CollectorRangeBox());

        }
        if (ModBlocks.TRUE_GREENSCREEN.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(
                BlockTrueGreenscreen.TileEntityTrueGreenscreen.class,
                new TESRTrueGreenscreen());
        }

        if (ModItems.GLOVE.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.GLOVE.get(), new GloveRenderer());
        }
        if (ModItems.FIRE_BATTERY.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.FIRE_BATTERY.get(), new FireBatteryRenderer());
        }
        if (ModItems.CHUNCHUNMARU.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.CHUNCHUNMARU.get(), new ChunchunmaruRenderer());
        }

        if (Mods.FindIt.isLoaded()) {
            FindItHelper.init();
        }
    }

    @SubscribeEvent
    public void onPostClientTick(TickEvent.ClientTickEvent event) {
        TESRTrueGreenscreen.onPostClientTick(event);
    }
}
