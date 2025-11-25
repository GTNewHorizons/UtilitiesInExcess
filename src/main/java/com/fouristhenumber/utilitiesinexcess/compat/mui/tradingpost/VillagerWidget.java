package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import java.util.List;

import net.minecraft.entity.IMerchant;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.fouristhenumber.utilitiesinexcess.common.wrappers.MerchantRecipeListWrapper;

public class VillagerWidget extends Column {

    private final PosGuiData data;
    private final PanelSyncManager manager;

    public VillagerWidget(PosGuiData data, PanelSyncManager manager, IMerchant merchant) {
        super();
        this.data = data;
        this.manager = manager;

        if (merchant != null) setSyncHandler(
            new VillagerSyncHandler(this, data, new MerchantRecipeListWrapper(merchant, data.getPlayer())));
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
                    .columnSyncHandler((VillagerSyncHandler) this.getSyncHandler());
            } else {
                child(
                    new TradeWidget(recipe).index(i)
                        .columnSyncHandler((VillagerSyncHandler) this.getSyncHandler()));
            }
        }
        scheduleResize();
    }

    public boolean matches(String search) {
        for  (IWidget widget : getChildren()) {
            if (widget instanceof TradeWidget && ((TradeWidget) widget).matches(search)) return true;
        }

        return false;
    }
}
