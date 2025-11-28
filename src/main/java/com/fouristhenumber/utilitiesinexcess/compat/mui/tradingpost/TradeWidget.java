package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.village.MerchantRecipe;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;
import com.fouristhenumber.utilitiesinexcess.utils.mui.TooltipItemDisplayWidget;

public class TradeWidget extends ParentWidget<TradeWidget> implements Interactable {

    // public void setSyncHandler(TradeWidgetSH handler) {
    // super.setSyncHandler(handler);
    // }

    public MerchantRecipe recipe;
    private int index;
    private VillagerSyncHandler columnSyncHandler;
    private boolean isFavorite = false;
    private boolean isDummy = false;

    private final TooltipItemDisplayWidget itemToBuy;
    private final TooltipItemDisplayWidget itemToBuy2;
    private final TooltipItemDisplayWidget itemToSell;

    public TradeWidget(MerchantRecipe _recipe) {
        super();
        this.recipe = _recipe;

        background(GuiTextures.BUTTON_CLEAN);
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
        ProgressWidget progress = new ProgressWidget().direction(ProgressWidget.Direction.RIGHT)
            .texture(GuiTextures.PROGRESS_ARROW, 20)
            .progress(() -> {
                var trade = getRecipe();
                if (trade == null) return 0.69;
                return 1 - (((AccessorMerchantRecipe) trade).getCurrentUses()
                    / (double) (((AccessorMerchantRecipe) trade).getMaxUses()));
            });
        Flow wholeRow = new Row().coverChildren()
            .childPadding(2)
            .child(inputItems)
            .child(progress)
            .child(itemToSell);
        this.child(wholeRow);
    }

    public static void buildToolTip(RichTooltip tooltip) {
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.trade_tooltip.0"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.trade_tooltip.1"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.trade_tooltip.2"));
        if (Mods.FindIt.isLoaded())
            tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.trade_tooltip.3"));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
        super.draw(context, widgetTheme);
        if (isFavorite) {
            Color.setGlColorOpaque(Color.YELLOW.main);
            GuiTextures.FAVORITE.draw(0, 0, 6, 6);
        }
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
