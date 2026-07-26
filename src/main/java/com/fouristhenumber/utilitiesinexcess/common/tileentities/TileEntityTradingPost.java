package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.text.StringKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.ScrollArea;
import com.cleanroommc.modularui.widget.scroll.ScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.SearchBar;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerColumn;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerEntityDisplay;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerWidget;
import com.gtnewhorizon.gtnhlib.client.VillagerNames;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    public static ArrayList<VillagerColumn> villagerColumns;

    // Trading post UI hierarchy:
    // panel > mainColumn > (topRow > villagercount) - (TradeList > tradeListRow >
    // 3x(VillagerColumn > VillagerWidget > TradeWidget))
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        villagerColumns = new ArrayList<>();

        TradingPostPanel panel = new TradingPostPanel("uie:trading_post");
        panel.height(226)
            .width(259);
        Flow mainColumn = Flow.column();
        mainColumn.height(229)
            .width(265)
            .pos(0, 0)
            .paddingRight(2)
            .childPadding(0);

        TradeList tradeList = new TradeList().coverChildrenWidth()
            .height(125)
            .pos(6, 15);
        Flow tradeListRow = Flow.row();
        tradeListRow.coverChildren()
            .childPadding(3)
            .left(0);
        for (int i = 0; i < 3; i++) {
            VillagerColumn columnOfVillagers = new VillagerColumn();
            columnOfVillagers.anchorTop(0)
                .coverChildren()
                .childPadding(3);

            villagerColumns.add(columnOfVillagers);

            tradeListRow.child(columnOfVillagers);
        }

        List<IMerchant> merchants = getMerchants();
        int i = 0;
        for (IMerchant merchant : merchants) {
            VillagerWidget villagerTrades = new VillagerWidget(
                guiData,
                guiSyncManager,
                merchant,
                ((VillagerColumn) tradeListRow.getChildren()
                    .get(i)));

            villagerTrades.tradingPostPanel(panel)
                .setColumnNumber(i)
                .coverChildren()
                .childPadding(3);

            ((VillagerColumn) tradeListRow.getChildren()
                .get(i)).child(villagerTrades);

            i = i < 2 ? i + 1 : 0;
        }
        tradeList.child(tradeListRow);

        Flow topRow = Flow.row();
        topRow.anchorLeft(0)
            .coverChildrenWidth()
            .height(10);
        topRow.child(
            new HelpWidget(Color.BLUE.main).top(2)
                .left(1)
                .size(12)
                .paddingRight(2)
                .tooltipBuilder(TileEntityTradingPost::buildHelpToolTip));
        topRow.child(
            IKey.str(
                StatCollector.translateToLocalFormatted("uie.gui.text.trading_post.villager_count", merchants.size()))
                .asWidget()
                .left(14)
                .top(4));
        panel.child(
            new SearchBar().villagerParent(tradeListRow)
                .top(4)
                .right(4)
                .height(10)
                .width(70));

        mainColumn.child(topRow);
        mainColumn.child(
            tradeList.margin(5, 5)
                .marginRight(10));
        Flow bottomRow = Flow.row();
        bottomRow.coverChildrenHeight()
            .width(259)
            .pos(0, 229 - 85);

        var inventory = SlotGroupWidget.playerInventory(0, false);
        bottomRow.child(
            inventory.marginBottom(6)
                .paddingRight(6)
                .anchorRight(1));
        bottomRow.child(
            new VillagerEntityDisplay(() -> VillagerWidget.lastVillager).left(26)
                .bottom(20));
        bottomRow.child(
            Flow.row()
                .child(
                    new MerchantNameKey().asWidget()
                        .center())
                .width(86)
                .height(26)
                .anchorBottom(1));
        mainColumn.child(bottomRow);
        panel.child(mainColumn);
        return panel;
    }

    public List<IMerchant> getMerchants() {
        // Baby villagers shouldn't trade :P
        AxisAlignedBB boundingBox = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord)
            .expand(32, this.worldObj.getHeight(), 32);
        return this.worldObj.selectEntitiesWithinAABB(IMerchant.class, boundingBox, entity -> {
            if (entity instanceof EntityAgeable ageable) return ageable.getGrowingAge() >= 0;
            return true;
        });
    }

    public static class TradeList extends ListWidget<Flow, TradeList> {

        @Override
        public void onInit() {
            if (getScrollData() == null) {
                scrollDirection(new TradeListScrollData());
            }
        }
    }

    public static class TradeListScrollData extends VerticalScrollData {

        // For some reason for the first couple of frames the scroll bar is in the wrong position
        // I've seen this phenomenon in other people's PRs, but I don't know if this is an MUI2 bug or not
        // So for now, I'm just going to hard-code it's x position.
        @Override
        public void drawScrollbar(ScrollArea area, ModularGuiContext context, WidgetTheme widgetTheme,
            IDrawable texture) {
            boolean isOtherActive = isOtherScrollBarActive(area, true);
            int l = this.getScrollBarLength(area);
            int y = 0;
            int w = getThickness();
            int h = area.height;
            GuiDraw.drawRect(243, y, w, h, area.getScrollBarBackgroundColor());

            y = getScrollBarStart(area, l, isOtherActive);
            ScrollData data2 = getOtherScrollData(area);
            if (data2 != null && isOtherActive && data2.isOnAxisStart()) {
                y += data2.getThickness();
            }
            h = l;
            drawScrollBar(context, 243, y, w, h, widgetTheme, texture);
        }
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    public static class HelpWidget extends Widget<HelpWidget> {

        private final int color;

        public HelpWidget(int color) {
            super();
            this.color = color;
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            super.draw(context, widgetTheme);
            Color.setGlColorOpaque(color);
            GuiTextures.HELP.draw(0, 0, 12, 12);
        }
    }

    public static void buildHelpToolTip(RichTooltip tooltip) {
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.0"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.1"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.2"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.3"));
        if (Mods.FindIt.isLoaded())
            tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.4"));
        tooltip.addLine("§7"); // If the line is empty it gets skipped
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.5"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.6"));
        tooltip.addLine(
            StatCollector.translateToLocalFormatted(
                "uie.gui.tooltip.trading_post.help.7",
                StatCollector.translateToLocal("uie.gui.tooltip.trading_post.search.buy_prefix"),
                StatCollector.translateToLocal("uie.gui.tooltip.trading_post.search.sell_prefix")));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.8"));
        tooltip.addLine(
            StatCollector.translateToLocalFormatted(
                "uie.gui.tooltip.trading_post.help.9",
                StatCollector.translateToLocal("uie.gui.tooltip.trading_post.search.sell_prefix")));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.10"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.11"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.12"));
        tooltip.addLine(StatCollector.translateToLocal("uie.gui.tooltip.trading_post.help.13"));
    }

    public class TradingPostPanel extends ModularPanel {

        // I love boiler-plate code
        public TradingPostPanel(@NotNull String name) {
            super(name);
        }

        public int[] columnCounts = new int[3];

        @Override
        public void onOpen(ModularScreen screen) {
            super.onOpen(screen);

            // These are used to keep track of how many villagers are in each column while initializing
            // the villager widgets
            columnCounts = new int[3];
        }
    }

    public class MerchantNameKey extends StringKey {

        public MerchantNameKey() {
            super("");
        }

        @Override
        public String get() {
            if (VillagerWidget.lastVillager instanceof EntityVillager villager) {
                return getVillagerDisplayName(villager);
            }

            return "";
        }
    }

    public static String getVillagerDisplayName(EntityVillager villager) {
        String key = "description.villager.profession." + VillagerNames.getVillagerName(villager.getProfession());
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return "";
    }
}
