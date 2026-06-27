package com.fouristhenumber.utilitiesinexcess.compat.nei;

import static com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilActive.getPseudoInversionChestAtDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class PseudoInversionRecipeHandler extends TemplateRecipeHandler {

    public record ChestGroup(String label, int required, List<ItemStack> validItems) {}

    private List<ChestGroup> groups;

    public PseudoInversionRecipeHandler() {
        buildGroups();
    }

    private void buildGroups() {
        groups = List.of(
            new ChestGroup(
                "nei.pseudo_inversion.label_north",
                InversionConfig.northChestRequiredItems,
                getPseudoInversionChestAtDirection(ForgeDirection.NORTH)),
            new ChestGroup(
                "nei.pseudo_inversion.label_south",
                InversionConfig.southChestRequiredItems,
                getPseudoInversionChestAtDirection(ForgeDirection.SOUTH)),
            new ChestGroup(
                "nei.pseudo_inversion.label_east",
                InversionConfig.eastChestRequiredItems,
                getPseudoInversionChestAtDirection(ForgeDirection.EAST)),
            new ChestGroup(
                "nei.pseudo_inversion.label_west",
                InversionConfig.westChestRequiredItems,
                getPseudoInversionChestAtDirection(ForgeDirection.WEST)));
    }

    @Override
    public String getOverlayIdentifier() {
        return "pseudo_inversion_recipes";
    }

    @Override
    public String getHandlerId() {
        return "pseudo_inversion_recipes";
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("nei.title.uie.pseudo_inversion");
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
            if (result.getItem() == ModItems.PSEUDO_INVERSION_SIGIL.get()) {
                arecipes.add(new CachedRitual(groups));
            }
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals(getOverlayIdentifier())) {
            arecipes.add(new CachedRitual(groups));
        } else if (inputId.equals("item") && ingredients.length > 0 && ingredients[0] instanceof ItemStack ingredient) {
            if (ingredient.getItem() == ModItems.PSEUDO_INVERSION_SIGIL.get()
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
        GL11.glColor4f(1f, 1f, 1f, 1f);
        CachedRitual cached = (CachedRitual) arecipes.get(recipe);
        for (PositionedStack ps : cached.getIngredients()) {
            GuiDraw.drawRect(ps.relx, ps.rely, 17, 17, 0xFFFFFFFF);
            GuiDraw.drawRect(ps.relx - 1, ps.rely - 1, 17, 17, 0xFF000000);
            GuiDraw.drawRect(ps.relx, ps.rely, 16, 16, 0xFF8B8B8B);
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
                StatCollector.translateToLocalFormatted("nei.pseudo_inversion.requires", group.required()),
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

        private final List<ChestGroup> groups;

        public CachedRitual(List<ChestGroup> groups) {
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

        public List<ChestGroup> getGroups() {
            return groups;
        }
    }
}
