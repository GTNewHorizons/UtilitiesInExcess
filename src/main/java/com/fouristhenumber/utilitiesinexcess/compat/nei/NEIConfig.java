package com.fouristhenumber.utilitiesinexcess.compat.nei;

import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
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
        EnderLocusRecipeHandler handler = new EnderLocusRecipeHandler();

        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);

        API.addRecipeCatalyst(ModBlocks.ENDER_LOCUS.newItemStack(), handler, 1);
        API.addRecipeCatalyst(ModBlocks.CONVERGENCE_CRYSTAL.newItemStack(), handler, 0);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.ender_locus")
                + "@ender_locus_recipes");
    }

    @SubscribeEvent
    public void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        event.registerHandlerInfo(
            new HandlerInfo.Builder("ender_locus_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                .setHeight(140)
                .setWidth(166)
                .setDisplayStack(ModBlocks.ENDER_LOCUS.newItemStack())
                .build());
    }
}
