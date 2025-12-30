package com.fouristhenumber.utilitiesinexcess.utils.mui;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.MCHelper;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.text.RichText;
import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerIngredientProvider;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;

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
    // For latest use
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

    public void buildTooltip(ItemStack stack, RichTooltip tooltip) {
        if (stack == null) return;
        tooltip.addFromItem(stack);
    }

    @Override
    // For latest use
    public @Nullable ItemStack getStackForRecipeViewer() {
        return getItemStack();
    }

    public boolean matches(String search) {
        ItemStack itemStack = getItemStack();
        if (itemStack == null) return false;

        return itemStack.getDisplayName()
            .toLowerCase()
            .contains(search)
            || itemStack.getItem()
                .getItemStackDisplayName(itemStack)
                .toLowerCase()
                .contains(search)
            || tooltipMatches(search);
    }

    private boolean tooltipMatches(String search) {
        if (!(tooltip().getRichText() instanceof RichText)) return false;

        for (String s : MCHelper.getItemToolTip(getItemStack())) {
            if (s.toLowerCase()
                .contains(search)) return true;
        }

        return false;
    }
}
