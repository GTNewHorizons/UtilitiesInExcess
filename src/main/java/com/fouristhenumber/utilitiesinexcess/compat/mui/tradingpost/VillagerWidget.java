package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost;
import com.fouristhenumber.utilitiesinexcess.common.wrappers.MerchantRecipeListWrapper;

public class VillagerWidget extends Column {

    private final PosGuiData data;
    private final PanelSyncManager manager;
    private final VillagerSyncHandler villagerSyncHandler;
    private final VillagerColumn villagerColumn;
    private boolean initializedRecipes;
    private TileEntityTradingPost.TradingPostPanel tradingPostPanel;
    private int columnNumber;

    public static EntityLivingBase lastVillager;

    public VillagerWidget(PosGuiData data, PanelSyncManager manager, IMerchant merchant,
        VillagerColumn villagerColumn) {
        super();
        this.data = data;
        this.manager = manager;
        background(GuiTextures.BUTTON_CLEAN);

        if (merchant != null) {
            villagerSyncHandler = new VillagerSyncHandler(
                this,
                data,
                new MerchantRecipeListWrapper(merchant, data.getPlayer()));
            setSyncHandler(villagerSyncHandler);
        } else {
            villagerSyncHandler = null;
        }

        this.villagerColumn = villagerColumn;
        initializedRecipes = false;
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        return syncHandler instanceof VillagerSyncHandler;
    }

    public void syncRecipes(MerchantRecipeListWrapper recipeList) {
        MerchantRecipeList merchantRecipeList = recipeList.getRecipeList();
        List<IWidget> children = getChildren();

        for (int i = 0; i < merchantRecipeList.size(); i++) {
            MerchantRecipe recipe = (MerchantRecipe) merchantRecipeList.get(i);

            if (i < children.size()) {
                ((TradeWidget) children.get(i)).setRecipe(recipe)
                    .index(i)
                    .favorite(recipeList.isFavorite(i))
                    .columnSyncHandler((VillagerSyncHandler) this.getSyncHandler());
            } else {
                child(
                    new TradeWidget(recipe).index(i)
                        .favorite(recipeList.isFavorite(i))
                        .columnSyncHandler((VillagerSyncHandler) this.getSyncHandler()));
            }
        }

        if (!initializedRecipes && data.getWorld().isRemote) {
            setEnabled(matches(SearchBar.prevText.toLowerCase()));

            if (isEnabled()) {
                if (isFavorite()) {
                    villagerColumn.moveChild(this, 0);
                } else {
                    villagerColumn.moveChild(this, tradingPostPanel.columnCounts[columnNumber]);
                    tradingPostPanel.columnCounts[columnNumber]++;
                }
            }

            initializedRecipes = true;
        }

        scheduleResize();
    }

    public boolean matches(String search) {
        for (IWidget widget : getChildren()) {
            if (widget instanceof TradeWidget && ((TradeWidget) widget).matches(search)) return true;
        }
        if (villagerSyncHandler != null && villagerSyncHandler.getRecipeList() != null
            && villagerSyncHandler.getRecipeList()
                .getMerchant() != null
            && villagerSyncHandler.getRecipeList()
                .getMerchant() instanceof EntityLiving living
            && (living.getCustomNameTag()
                .toLowerCase()
                .contains(search)
                || (living instanceof EntityVillager villager && TileEntityTradingPost.getVillagerDisplayName(villager)
                    .toLowerCase()
                    .contains(search))))
            return true;

        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!data.getWorld().isRemote) return;

        if (isHovering() || isBelowMouse()) {
            if (villagerSyncHandler.getRecipeList()
                .getMerchant() instanceof EntityLivingBase entityMerchant) {
                lastVillager = entityMerchant;
            }
        }
    }

    public boolean isFavorite() {
        for (IWidget widget : getChildren()) {
            TradeWidget tradeWidget = (TradeWidget) widget;
            if (tradeWidget.isFavorite()) return true;
        }
        return false;
    }

    public VillagerWidget tradingPostPanel(TileEntityTradingPost.TradingPostPanel tradingPostPanel) {
        this.tradingPostPanel = tradingPostPanel;
        return this;
    }

    public VillagerWidget setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
        return this;
    }
}
