package com.fouristhenumber.utilitiesinexcess.compat.mui;

import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class TooltipItemDisplayWidget extends ItemDisplayWidget {
    public TooltipItemDisplayWidget()
    {
        super();
        tooltip().setAutoUpdate(true);//.setHasTitleMargin(true);
        tooltipBuilder(tooltip -> {
            ItemStack stack = getItemStack();
            buildTooltip(stack, tooltip);
        });
    }

    public ItemStack getItemStack()
    {
        var v=(ItemStack)getValue().getValue();
        return v;
    }

    public boolean doDisplayAmount = false;
    @Override
    public ItemDisplayWidget displayAmount(boolean displayAmount) {
        this.doDisplayAmount=displayAmount;
        return super.displayAmount(displayAmount);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
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

}
