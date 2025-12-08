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
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.text.StringKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.SearchBar;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerColumn;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerEntityDisplay;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerWidget;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    public static ArrayList<VillagerColumn> villagerColumns;

    // Trading post UI heirarchy:
    // panel > mainColumn > (topRow > villagercount) - (TradeList > tradeListRow >
    // 3x(VillagerColumn > VillagerWidget > TradeWidget))
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        villagerColumns = new ArrayList<>();

        TradingPostPanel panel = new TradingPostPanel("uie:trading_post");
        panel.height(229)
            .width(265);
        Column mainColumn = new Column();
        mainColumn.sizeRel(1)
            .paddingRight(2)
            .childPadding(0);

        TradeList tradeList = new TradeList().coverChildrenWidth()
            .padding(0)
            .margin(0)
            .paddingRight(1)
            .height(125);
        Row tradeListRow = new Row();
        tradeListRow.coverChildren()
            .height(1500)
            .childPadding(3)
            .paddingRight(2);
        for (int i = 0; i < 3; i++) {
            VillagerColumn columnOfVillagers = new VillagerColumn();
            columnOfVillagers.alignY(0)
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

        Row topRow = new Row();
        topRow.alignX(0)
            .coverChildrenWidth()
            .height(10);
        topRow.child(
            new HelpWidget().top(2)
                .left(1)
                .size(12)
                .paddingRight(2)
                .tooltipBuilder(TileEntityTradingPost::buildHelpToolTip));
        topRow.child(
            IKey.str(StatCollector.translateToLocalFormatted("tile.trading_post.villager_count", merchants.size()))
                .asWidget()
                .left(14)
                .top(4));
        panel.child(
            new SearchBar().villagerParent(tradeListRow)
                .alignX(1)
                .alignY(0)
                .top(4)
                .right(1)
                .height(10)
                .width(70));

        mainColumn.child(topRow);
        mainColumn.child(
            tradeList.margin(5, 5)
                .marginRight(10));
        Row bottomRow = new Row();
        bottomRow.coverChildrenHeight()
            .alignX(1)
            .width(256);

        var inventory = SlotGroupWidget.playerInventory(0, false);
        bottomRow.child(
            inventory.marginBottom(6)
                .paddingRight(6)
                .alignX(1));
        bottomRow.child(
            new VillagerEntityDisplay(() -> VillagerWidget.lastVillager).left(26)
                .bottom(20));
        bottomRow.child(
            new Row().child(
                new MerchantNameKey().asWidget()
                    .center())
                .width(86)
                .height(26)
                .alignY(1));
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

    public static class TradeList extends ListWidget<Row, TradeList> {

    }

    public static class HelpWidget extends Widget<HelpWidget> {

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            super.draw(context, widgetTheme);
            Color.setGlColorOpaque(Color.BLUE.main);
            GuiTextures.HELP.draw(0, 0, 12, 12);
        }
    }

    public static void buildHelpToolTip(RichTooltip tooltip) {
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.0"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.1"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.2"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.3"));
        if (Mods.FindIt.isLoaded()) tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.4"));
        tooltip.addLine("§7"); // If the line is empty it gets skipped
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.5"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.6"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.7"));
        tooltip.addLine(StatCollector.translateToLocal("tile.trading_post.help_tooltip.8"));
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
        String key = "tile.trading_post.villager_name." + villager.getProfession();
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return "";
    }
}
