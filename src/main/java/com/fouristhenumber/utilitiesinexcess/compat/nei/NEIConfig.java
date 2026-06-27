package com.fouristhenumber.utilitiesinexcess.compat.nei;

import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.Tags;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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

        PseudoInversionRecipeHandler pseudoInversionHandler = new PseudoInversionRecipeHandler();

        API.registerRecipeHandler(pseudoInversionHandler);
        API.registerUsageHandler(pseudoInversionHandler);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.pseudo_inversion")
                + "@pseudo_inversion_recipes");
    }

    @SubscribeEvent
    public void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        event.registerHandlerInfo(
            new HandlerInfo.Builder("ender_locus_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                .setHeight(140)
                .setWidth(166)
                .setDisplayStack(ModBlocks.ENDER_LOCUS.newItemStack())
                .build());
        event.registerHandlerInfo(
            new HandlerInfo.Builder("pseudo_inversion_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                .setHeight(140)
                .setWidth(166)
                .setDisplayStack(ModItems.PSEUDO_INVERSION_SIGIL.newItemStack())
                .build());
    }
}
