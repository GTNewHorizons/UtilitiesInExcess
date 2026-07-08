package com.fouristhenumber.utilitiesinexcess.compat.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilActive;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class PseudoReversionRecipeHandler extends TemplateRecipeHandler {

    public record ChestGroup(String label, int required, List<ItemStack> validItems) {}

    private ChestGroup[] groups;

    public PseudoReversionRecipeHandler() {
        buildGroups();
    }

    private void buildGroups() {
        groups = new ChestGroup[] {
            new ChestGroup(
                "uie.nei.text.pseudo_reversion.label_north",
                InversionConfig.INSTANCE.northChestRequiredItems,
                ItemInversionSigilActive.getPseudoReversionChestAtDirection(ForgeDirection.NORTH)),
            new ChestGroup(
                "uie.nei.text.pseudo_reversion.label_south",
                InversionConfig.INSTANCE.southChestRequiredItems,
                ItemInversionSigilActive.getPseudoReversionChestAtDirection(ForgeDirection.SOUTH)),
            new ChestGroup(
                "uie.nei.text.pseudo_reversion.label_east",
                InversionConfig.INSTANCE.eastChestRequiredItems,
                ItemInversionSigilActive.getPseudoReversionChestAtDirection(ForgeDirection.EAST)),
            new ChestGroup(
                "uie.nei.text.pseudo_reversion.label_west",
                InversionConfig.INSTANCE.westChestRequiredItems,
                ItemInversionSigilActive.getPseudoReversionChestAtDirection(ForgeDirection.WEST)) };
    }

    @Override
    public String getOverlayIdentifier() {
        return "pseudo_reversion_recipes";
    }

    @Override
    public String getHandlerId() {
        return "pseudo_reversion_recipes";
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("uie.nei.title.pseudo_reversion");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            arecipes.add(new CachedRitual(groups));
        } else if (outputId.equals("item") && results.length > 0 && results[0] instanceof ItemStack result) {
            if (result.getItem() == ModItems.PSEUDO_REVERSION_SIGIL.get()) {
                arecipes.add(new CachedRitual(groups));
            }
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals(getOverlayIdentifier())) {
            arecipes.add(new CachedRitual(groups));
        } else if (inputId.equals("item") && ingredients.length > 0 && ingredients[0] instanceof ItemStack ingredient) {
            if (ingredient.getItem() == ModItems.PSEUDO_REVERSION_SIGIL.get()
                || ingredient.getItem() == ModItems.INVERSION_SIGIL_ACTIVE.get()) {
                arecipes.add(new CachedRitual(groups));
            }
        }
    }

    private int computeContentHeight() {
        int h = 2;
        for (ChestGroup group : groups) {
            // Two labels per group
            h += 20;
            int rowCount = (int) Math.ceil(
                group.validItems()
                    .size() / (double) 9);
            h += rowCount * 18 + 3;
        }
        return h + 2;
    }

    @Override
    public int getRecipeHeight(int recipe) {
        return computeContentHeight();
    }

    @Override
    public void drawBackground(int recipe) {
        GuiDraw.changeTexture(getGuiTexture());

        CachedRitual cached = (CachedRitual) arecipes.get(recipe);
        for (PositionedStack ps : cached.getIngredients()) {
            GuiDraw.drawTexturedModalRect(ps.relx - 1, ps.rely - 1, 7, 83, 18, 18);
        }
    }

    @Override
    public void drawExtras(int recipe) {
        int y = 2;
        for (ChestGroup group : ((CachedRitual) arecipes.get(recipe)).getGroups()) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GuiDraw.drawString(StatCollector.translateToLocalFormatted(group.label), 5, y, 0x404040, false);
            y += 10;
            GuiDraw.drawString(
                StatCollector.translateToLocalFormatted("uie.nei.text.pseudo_reversion.requires", group.required()),
                5,
                y,
                0x404040,
                false);
            y += 10;

            int rowCount = (int) Math.ceil(
                group.validItems()
                    .size() / (double) 9);
            y += rowCount * 18 + 3;
        }
    }

    public class CachedRitual extends CachedRecipe {

        private final ChestGroup[] groups;

        public CachedRitual(ChestGroup[] groups) {
            this.groups = groups;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> stacks = new ArrayList<>();
            int y = 2;
            for (ChestGroup group : groups) {
                y += 20;
                List<ItemStack> items = group.validItems();
                for (int i = 0; i < items.size(); i++) {
                    int col = i % 9;
                    int row = i / 9;
                    int x = 5 + col * 18;
                    stacks.add(new PositionedStack(items.get(i), x, y + row * 18));
                }
                int rowCount = (int) Math.ceil(items.size() / (double) 9);
                y += rowCount * 18 + 3;
            }
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        public ChestGroup[] getGroups() {
            return groups;
        }
    }
}
