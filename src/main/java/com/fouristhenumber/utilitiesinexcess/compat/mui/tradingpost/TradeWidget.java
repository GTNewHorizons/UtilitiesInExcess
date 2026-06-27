package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;

public class TradeWidget extends ParentWidget<TradeWidget> implements Interactable {

    public static final UITexture HIGHLIGHT_BACKGROUND = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/trade_highlight")
        .imageSize(18, 18)
        .name("trade_highlight")
        .canApplyTheme()
        .build();

    public MerchantRecipe recipe;
    private int index;
    private VillagerSyncHandler columnSyncHandler;
    private boolean isFavorite = false;

    private final TradeItemDisplayWidget itemToBuy;
    private final TradeItemDisplayWidget itemToBuy2;
    private final TradeItemDisplayWidget itemToSell;

    public static class TradeProgressWidget extends ProgressWidget {

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            super.draw(context, widgetTheme);
            float progress = getCurrentProgress();
            if (progress <= 0) {
                Color.setGlColorOpaque(Color.RED.main);
                GuiTextures.CLOSE.draw(0, 0, getArea().width, getArea().height);
            }
        }
    }

    public TradeWidget(MerchantRecipe _recipe) {
        super();
        this.recipe = _recipe;

        this.coverChildren();

        ItemStack item;
        if (_recipe != null) {
            item = new ItemStack(ModItems.INVERSION_SIGIL_ACTIVE.get());
        } else {
            item = new ItemStack(ModItems.INVERSION_SIGIL_INACTIVE.get());
        }

        itemToBuy = new TradeItemDisplayWidget();
        itemToBuy.setTradeItemType(TradeItemDisplayWidget.TradeItemType.BUY)
            .paddingRight(0)
            .displayAmount(true)
            .item(
                new ObjectValue.Dynamic<>(
                    ItemStack.class,
                    () -> this.recipe != null ? this.recipe.getItemToBuy() : item,
                    i -> {}));

        Flow inputItems = Flow.row()
            .childPadding(1)
            .coverChildren()
            .child(itemToBuy);

        if (this.recipe.getSecondItemToBuy() != null) {
            itemToBuy2 = new TradeItemDisplayWidget();
            itemToBuy2.setTradeItemType(TradeItemDisplayWidget.TradeItemType.BUY2)
                .paddingLeft(0)
                .displayAmount(true)
                .item(
                    new ObjectValue.Dynamic<>(
                        ItemStack.class,
                        () -> this.recipe != null ? this.recipe.getSecondItemToBuy() : item,
                        i -> {}));

            inputItems.child(itemToBuy2);
        } else {
            inputItems.child(new Widget<>().size(18));
            itemToBuy2 = null;
        }

        itemToSell = new TradeItemDisplayWidget();
        itemToSell.setTradeItemType(TradeItemDisplayWidget.TradeItemType.SELL)
            .displayAmount(true)
            .item(
                new ObjectValue.Dynamic<>(
                    ItemStack.class,
                    () -> this.recipe != null ? this.recipe.getItemToSell() : item,
                    i -> {}));

        ProgressWidget progress = new TradeProgressWidget().direction(ProgressWidget.Direction.RIGHT)
            .texture(GuiTextures.PROGRESS_ARROW, 20)
            .value(new DoubleValue.Dynamic(() -> {
                AccessorMerchantRecipe trade = (AccessorMerchantRecipe) getRecipe();
                if (trade == null) return 0.69;
                if (trade.getMaxUses() > 7) // After the initial 7 uses every recipe gets 2 to 12 additional uses
                    return ((double) trade.getMaxUses() - trade.getCurrentUses()) / 12;
                return 1 - (trade.getCurrentUses() / (double) (trade.getMaxUses()));
            }, null));
        Flow wholeRow = Flow.row()
            .coverChildren()
            .padding(1)
            .childPadding(2)
            .child(inputItems)
            .child(progress)
            .child(itemToSell);
        this.child(wholeRow);

        hoverBackground(HIGHLIGHT_BACKGROUND);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.draw(context, widgetTheme);

        if (index > 0) {
            Gui.drawRect(10, -2, getArea().width - 10, -1, 0x1FFFFFFF);
        }

        // Yes. Both this *and* hoverBackground(HIGHLIGHT_BACKGROUND) *and* the drawBackground override are needed.
        if (isBelowMouse() || isHovering()) Gui.drawRect(1, 1, getArea().width - 1, getArea().height - 1, 0xFFA1A1A1);
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {

    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean alt = Keyboard.isKeyDown(Keyboard.KEY_LMENU);
        columnSyncHandler.tradeClick(mouseButton, index, shift, alt);
        if (!alt && mouseButton == 0) {
            columnSyncHandler.executeTrade(index, shift);
        } else if (alt && mouseButton == 0) {
            isFavorite = !isFavorite;
        } else if (mouseButton == 1 && Mods.FindIt.isLoaded()) {
            columnSyncHandler.highLightVillager();
        }

        return Interactable.super.onMousePressed(mouseButton);
    }

    public MerchantRecipe getRecipe() {
        return recipe;
    }

    public TradeWidget setRecipe(MerchantRecipe r) {
        recipe = r;
        return this;
    }

    public TradeWidget columnSyncHandler(VillagerSyncHandler columnSyncHandler) {
        this.columnSyncHandler = columnSyncHandler;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public TradeWidget index(int index) {
        this.index = index;
        return this;
    }

    public TradeWidget favorite(boolean favorite) {
        this.isFavorite = favorite;
        return this;
    }

    public boolean isFavorite() {
        return this.isFavorite;
    }

    public boolean matches(String search) {
        return itemToBuy.matches(search) || (itemToBuy2 != null && itemToBuy2.matches(search))
            || itemToSell.matches(search);
    }
}
