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
        QEDRecipeHandler handler = new QEDRecipeHandler();

        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);

        API.addRecipeCatalyst(ModBlocks.QED.newItemStack(), handler, 1);
        API.addRecipeCatalyst(ModBlocks.FLUX_CRYSTAL.newItemStack(), handler, 0);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.qed") + "@qed_recipes");
    }

    @SubscribeEvent
    public void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        event.registerHandlerInfo(
            new HandlerInfo.Builder("qed_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID).setHeight(140)
                .setWidth(166)
                .setDisplayStack(ModBlocks.QED.newItemStack())
                .build());
    }
}
