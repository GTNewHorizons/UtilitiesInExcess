package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;
import com.fouristhenumber.utilitiesinexcess.utils.mui.TooltipItemDisplayWidget;

public class TradeWidget extends ParentWidget<TradeWidget> implements Interactable {

    // public void setSyncHandler(TradeWidgetSH handler) {
    // super.setSyncHandler(handler);
    // }

    public MerchantRecipe recipe;
    private int index;
    private VillagerSyncHandler columnSyncHandler;
    private boolean isDummy = false;

    private final TooltipItemDisplayWidget itemToBuy;
    private final TooltipItemDisplayWidget itemToBuy2;
    private final TooltipItemDisplayWidget itemToSell;

    public TradeWidget(MerchantRecipe _recipe) {
        super();
        this.recipe = _recipe;

        background(GuiTextures.BUTTON_CLEAN);
        this.coverChildren();
        // this.child(
        // IKey.str("*")
        // .asWidget()
        // .margin(0, 0, 0, 0)
        // .setEnabledIf(w -> getRecipe() != null && getRecipe().getFavorite()));

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

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (mouseButton == 0 && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            // getRecipe().setFavorite(!getRecipe().getFavorite());
            // this.getHandler()
            // .syncToServer(0, buffer -> { buffer.writeBoolean(getRecipe().getFavorite()); });
            return Result.SUCCESS;
        }

        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        columnSyncHandler.tradeClick(index, shift);
        columnSyncHandler.executeTrade(index, shift);

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

    public TradeWidget dummy() {
        this.isDummy = true;
        return this;
    }

    public boolean matches(String search) {
        return (itemToBuy.matches(search) || itemToBuy2.matches(search) || itemToSell.matches(search)) && !isDummy;
    }
}
