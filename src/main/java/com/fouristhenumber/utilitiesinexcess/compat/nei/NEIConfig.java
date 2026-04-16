package com.fouristhenumber.utilitiesinexcess.compat.nei;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

@SuppressWarnings("unused") // automatically found by NEI
public class NEIConfig implements IConfigureNEI {

    @Override
    public String getName() {
        return "UtilitiesInExcess NEI Plugin";
    }

    @Override
    public String getVersion() {
        return "1.0"; // ??
    }

    @Override
    public void loadConfig() {
        addHandler(new QEDRecipeHandler());
    }

    private void addHandler(TemplateRecipeHandler handler) {
        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + handler.getRecipeName() + "@" + handler.getOverlayIdentifier());
        GuiCraftingRecipe.craftinghandlers.add(handler);
        GuiUsageRecipe.usagehandlers.add(handler);
    }
}
