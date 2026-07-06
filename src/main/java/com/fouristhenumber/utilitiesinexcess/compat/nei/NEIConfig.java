package com.fouristhenumber.utilitiesinexcess.compat.nei;

import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.Tags;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    @Override
    public String getName() {
        return StatCollector.translateToLocal("nei.title.uie.plugin");
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }

    @Override
    public void loadConfig() {
        EnderLocusRecipeHandler enderLocusRecipeHandler = new EnderLocusRecipeHandler();

        API.registerRecipeHandler(enderLocusRecipeHandler);
        API.registerUsageHandler(enderLocusRecipeHandler);

        API.addRecipeCatalyst(ModBlocks.ENDER_LOCUS.newItemStack(), enderLocusRecipeHandler, 1);
        API.addRecipeCatalyst(ModBlocks.CONVERGENCE_CRYSTAL.newItemStack(), enderLocusRecipeHandler, 0);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.ender_locus")
                + "@ender_locus_recipes");

        PseudoReversionRecipeHandler pseudoReversionHandler = new PseudoReversionRecipeHandler();

        API.registerRecipeHandler(pseudoReversionHandler);
        API.registerUsageHandler(pseudoReversionHandler);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.pseudo_reversion")
                + "@pseudo_reversion_recipes");
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber(side = Side.CLIENT)
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return Mods.NEI.isLoaded();
        }

        @SubscribeEvent
        public static void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
            event.registerHandlerInfo(
                new HandlerInfo.Builder("ender_locus_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                    .setHeight(140)
                    .setWidth(166)
                    .setDisplayStack(ModBlocks.ENDER_LOCUS.newItemStack())
                    .build());
            event.registerHandlerInfo(
                new HandlerInfo.Builder("pseudo_reversion_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                    .setHeight(140)
                    .setWidth(166)
                    .setDisplayStack(ModItems.PSEUDO_REVERSION_SIGIL.newItemStack())
                    .build());
        }
    }
}
