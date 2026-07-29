package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ItemRetrievalNodeLogic;
import net.minecraft.item.ItemStack;

public class TileEntityItemRetrievalNode extends TileEntityTransferNodeBase<ItemRetrievalNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<ItemStack>
{
    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return null;
    }

    @Override
    public void updateSource()
    {

    }

    @Override
    protected ItemRetrievalNodeLogic createLogic() {
        return null;
    }

    @Override
    public ItemStack getWalkingObject() {
        return null;
    }
}
