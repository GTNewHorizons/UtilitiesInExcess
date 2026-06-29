package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.MCHelper;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.text.RichText;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.utils.mui.TooltipItemDisplayWidget;

public class TradeItemDisplayWidget extends TooltipItemDisplayWidget {

    public enum TradeItemType {
        BUY,
        BUY2,
        SELL
    }

    private TradeItemType tradeItemType;

    public TradeItemDisplayWidget setTradeItemType(TradeItemType tradeItemType) {
        this.tradeItemType = tradeItemType;
        return this;
    }

    public TradeItemType getTradeItemType() {
        return tradeItemType;
    }

    // We draw the favorite icon here so it can be drawn over the first item sold by the trade
    // This can't be done in its own widget because that widget would block hovering over the first item in the trade.
    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.draw(context, widgetTheme);

        if (getTradeItemType() == TradeItemType.BUY && ((TradeWidget) (getParent().getParent()
            .getParent())).isFavorite()) {
            Color.setGlColorOpaque(0xFFFFAA00);
            GuiTextures.FAVORITE.draw(-1, -2, 10, 10);
        }
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
            || tooltipMatches(search)
            || (search.startsWith(StatCollector.translateToLocal("tile.trading_post.search.buy_prefix"))
                && (getTradeItemType() == TradeItemType.BUY || getTradeItemType() == TradeItemType.BUY2)
                && itemStack.getItem()
                    .getItemStackDisplayName(itemStack)
                    .toLowerCase()
                    .contains(
                        search.substring(
                            StatCollector.translateToLocal("tile.trading_post.search.buy_prefix")
                                .length())))
            || (search.startsWith(StatCollector.translateToLocal("tile.trading_post.search.sell_prefix"))
                && getTradeItemType() == TradeItemType.SELL
                && itemStack.getItem()
                    .getItemStackDisplayName(itemStack)
                    .toLowerCase()
                    .contains(
                        search.substring(
                            StatCollector.translateToLocal("tile.trading_post.search.sell_prefix")
                                .length())));
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
