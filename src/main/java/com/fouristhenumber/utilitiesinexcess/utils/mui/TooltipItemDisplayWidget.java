package com.fouristhenumber.utilitiesinexcess.utils.mui;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerIngredientProvider;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;

/**
 * Generic widget for displaying items with a hover tooltip and no background
 */
public class TooltipItemDisplayWidget extends ItemDisplayWidget implements RecipeViewerIngredientProvider {

    public TooltipItemDisplayWidget() {
        super();
        tooltip().setAutoUpdate(true)
            .titleMargin(1);
        tooltipBuilder(tooltip -> {
            ItemStack stack = getItemStack();
            buildTooltip(stack, tooltip);
        });
        background(IDrawable.EMPTY);
    }

    public ItemStack getItemStack() {
        return (ItemStack) getValue().getValue();
    }

    public boolean doDisplayAmount = false;

    @Override
    public ItemDisplayWidget displayAmount(boolean displayAmount) {
        this.doDisplayAmount = displayAmount;
        return super.displayAmount(displayAmount);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        ItemStack item = getItemStack();
        if (!Platform.isStackEmpty(item)) {
            GuiDraw.drawItem(item, 1, 1, 16, 16, context.getCurrentDrawingZ());
            if (this.doDisplayAmount) {
                GuiDraw.drawStandardSlotAmountText(item.stackSize, null, getArea());
            }
            Platform.endDrawItem();
        }
    }

    @Override
    public void drawForeground(ModularGuiContext context) {
        RichTooltip tooltip = getTooltip();
        if (tooltip != null && isHoveringFor(tooltip.getShowUpTimer())) {
            tooltip.draw(context, getItemStack());
        }
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {

    }

    public void buildTooltip(ItemStack stack, RichTooltip tooltip) {
        if (stack == null) return;
        tooltip.addFromItem(stack);
    }

    @Override
    public @Nullable ItemStack getStackForRecipeViewer() {
        return getItemStack();
    }
}
