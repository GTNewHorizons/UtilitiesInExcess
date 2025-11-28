package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.List;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.SearchBar;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerColumn;
import com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost.VillagerWidget;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    // Trading post UI heirarchy:
    // panel > mainColumn > (topRow > (villagercount - SearchBar)) - (TradeList > tradeListRow >
    // 3x(VillagerColumn > VillagerWidget > TradeWidget))
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        List<IMerchant> lm = getMerchants();
        ModularPanel panel = new ModularPanel("uie:trading_post");
        panel.coverChildren();
        Column mainColumn = new Column();
        mainColumn.coverChildren();

        TradeList tradeList = new TradeList().coverChildrenWidth()
            .padding(0)
            .margin(0)
            .paddingRight(1)
            .height(125);
        Row tradeListRow = new Row();
        tradeListRow.coverChildren()
            .childPadding(1)
        // .paddingRight(2) // For latest
        ;
        for (int i = 0; i < 3; i++) {
            VillagerColumn columnOfVillagers = new VillagerColumn();
            columnOfVillagers.alignY(0)
                .coverChildren()
                .childPadding(2);

            // In MUI2 latest I had issues with the dynamic sizing of the panel
            // So these dummy trades were required to always keep it the same size
            // VillagerWidget villagerTrades2 = new VillagerWidget(guiData, guiSyncManager, null, columnOfVillagers);
            // villagerTrades2.child(
            // new TradeWidget(null).dummy()
            // .height(1))
            // .height(1)
            // .setEnabled(false);
            // columnOfVillagers.child(villagerTrades2);

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
            villagerTrades.coverChildren();
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
            IKey.str(StatCollector.translateToLocalFormatted("tile.trading_post.villager_count", merchants.size()))
                .asWidget()
                .left(5)
                .top(5));
        panel.child(
            new SearchBar().villagerParent(tradeListRow)
                .alignX(1)
                .alignY(0)
                .top(2)
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
            .width(174);

        var inventory = SlotGroupWidget.playerInventory(0, false);
        bottomRow.child(
            inventory.marginBottom(6)
                .paddingRight(6)
                .alignX(1));
        mainColumn.child(bottomRow);
        panel.child(mainColumn);
        return panel;
    }

    public List<IMerchant> getMerchants() {
        // Baby villagers shouldn't trade :P
        AxisAlignedBB boundingBox = this.getRenderBoundingBox()
            .expand(32, this.worldObj.getHeight(), 32);
        return this.worldObj.selectEntitiesWithinAABB(IMerchant.class, boundingBox, entity -> {
            if (entity instanceof EntityAgeable ageable) return ageable.getGrowingAge() >= 0;
            return true;
        });
    }

    public static class TradeList extends ListWidget<Row, TradeList> {

    }
}
