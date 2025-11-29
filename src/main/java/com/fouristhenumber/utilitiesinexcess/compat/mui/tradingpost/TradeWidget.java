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
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;
import com.fouristhenumber.utilitiesinexcess.utils.mui.TooltipItemDisplayWidget;

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
    private boolean isDummy = false;

    private final TooltipItemDisplayWidget itemToBuy;
    private final TooltipItemDisplayWidget itemToBuy2;
    private final TooltipItemDisplayWidget itemToSell;

    public class TradeProgressWidget extends ProgressWidget {

        @Override
        public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
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

        itemToBuy = new TooltipItemDisplayWidget();
        itemToBuy.paddingRight(0)
            .displayAmount(true)
            .item(new ObjectValue.Dynamic<>(() -> this.recipe != null ? this.recipe.getItemToBuy() : item, i -> {}));

        itemToBuy2 = new TooltipItemDisplayWidget();
        itemToBuy2.paddingLeft(0)
            .displayAmount(true)
            .item(
                new ObjectValue.Dynamic<>(
                    () -> this.recipe != null ? this.recipe.getSecondItemToBuy() : item,
                    i -> {}));

        itemToSell = new TooltipItemDisplayWidget();
        itemToSell.displayAmount(true)
            .item(new ObjectValue.Dynamic<>(() -> this.recipe != null ? this.recipe.getItemToSell() : item, i -> {}));

        Flow inputItems = new Row().childPadding(1)
            .coverChildren()
            .child(itemToBuy)
            .child(itemToBuy2);
        ProgressWidget progress = new TradeProgressWidget().direction(ProgressWidget.Direction.RIGHT)
            .texture(GuiTextures.PROGRESS_ARROW, 20)
            .progress(() -> {
                AccessorMerchantRecipe trade = (AccessorMerchantRecipe) getRecipe();
                if (trade == null) return 0.69;
                if (trade.getMaxUses() > 7) // After the initial 7 uses every recipe gets 2 to 12 additional uses
                    return ((double) trade.getMaxUses() - trade.getCurrentUses()) / 12;
                return 1 - (trade.getCurrentUses() / (double) (trade.getMaxUses()));
            });
        Flow wholeRow = new Row().coverChildren()
            .padding(1)
            .childPadding(2)
            .child(inputItems)
            .child(progress)
            .child(itemToSell);
        this.child(wholeRow);

        hoverBackground(HIGHLIGHT_BACKGROUND);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
        super.draw(context, widgetTheme);

        if (index > 0) {
            Gui.drawRect(10, -2, getArea().width - 10, -1, 0x1FFFFFFF);
        }

        // Yes. Both this *and* hoverBackground(HIGHLIGHT_BACKGROUND) *and* the drawBackground override are needed.
        if (isBelowMouse() || isHovering()) Gui.drawRect(1, 1, getArea().width - 1, getArea().height - 1, 0xFFA1A1A1);

        if (isFavorite) {
            Color.setGlColorOpaque(Color.YELLOW.main);
            GuiTextures.FAVORITE.draw(0, 0, 6, 6);
        }
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetTheme widgetTheme) {

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

    public TradeWidget dummy() {
        this.isDummy = true;
        return this;
    }

    public boolean matches(String search) {
        return (itemToBuy.matches(search) || itemToBuy2.matches(search) || itemToSell.matches(search)) && !isDummy;
    }
}
