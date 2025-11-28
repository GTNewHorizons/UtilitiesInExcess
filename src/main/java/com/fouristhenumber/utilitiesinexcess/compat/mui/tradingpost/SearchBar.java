package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost;

public class SearchBar extends TextFieldWidget {

    Row villagerParent;

    UITexture VANILLA_SEARCH_BACKGROUND = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/vanilla_search")
        .imageSize(18, 18)
        .adaptable(1)
        .name("vanilla_search")
        .canApplyTheme()
        .build();

    public SearchBar() {
        super();
        background(VANILLA_SEARCH_BACKGROUND);
        hintText(StatCollector.translateToLocal("tile.trading_post.search_hint"));
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetTheme widgetTheme) {
        IDrawable bg = getCurrentBackground(context.getTheme(), widgetTheme);
        if (bg != null) {
            bg.draw(context, 2, 1, getArea().width - 4, getArea().height - 3, widgetTheme);
        }
    }

    public SearchBar villagerParent(Row parent) {
        villagerParent = parent;
        return this;
    }

    private String prevText = "";

    @Override
    public void onUpdate() {
        super.onUpdate();
        String txt = getText();

        if (!txt.equals(prevText)) {
            doSearch(txt);
            prevText = txt;
        }
    }

    public void doSearch(String search) {
        for (IWidget villagerColumn : villagerParent.getChildren()) {
            int foundCount = 0;
            for (int i = 0; i < villagerColumn.getChildren()
                .size(); i++) {
                VillagerWidget villagerWidget = (VillagerWidget) villagerColumn.getChildren()
                    .get(i);

                boolean isEnabled = villagerWidget.matches(search.toLowerCase());
                villagerWidget.setEnabled(isEnabled);
                if (isEnabled) {
                    ((VillagerColumn) villagerColumn).moveChild(i, foundCount);
                    foundCount++;
                }
                villagerWidget.scheduleResize();
            }
            villagerColumn.scheduleResize();
        }
        ((TileEntityTradingPost.TradeList) villagerParent.getParent()).getScrollData()
            .scrollTo(((TileEntityTradingPost.TradeList) villagerParent.getParent()).getScrollArea(), 0);
    }
}
