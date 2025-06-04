package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.utils.VoidItemHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTrashCanItem extends TileEntity implements IGuiHolder<PosGuiData> {

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        SlotGroup slotGroup = new SlotGroup("trash_can_inv", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        IItemHandler handler = new VoidItemHandler(1);
        panel.getPanel().child(
            new ItemSlot().slot(new ModularSlot(handler, 0).slotGroup(slotGroup))
                .align(Alignment.CENTER));
        return panel;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
}
