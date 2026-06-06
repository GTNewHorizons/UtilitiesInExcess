package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.NetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.TransferUpgrade;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.ItemFilter;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory.getInventoryContentsFromStack;

public class FilterPipeLogic extends NetworkLogic implements IInventory
{
    // Slot filters are organized in the same way that
    private final ItemStack[] filters = new ItemStack[6];


    private ItemFilter[] logicalFilter = new ItemFilter[6];

    public FilterPipeLogic(ITransferNetworkComponent host) {
        super(host);
    }

    @Override
    public boolean canConnectEnergy() {
        return true;
    }

    @Override
    public boolean canConnectFluid() {
        return true;
    }

    @Override
    public boolean canConnectItem() {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 6;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn)
    {
        return filters[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (filters[index] == null)
        {
            return null;
        }
        return filters[index].splitStack(count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return filters[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        filters[index] = stack;
        this.markDirty();
    }

    public String getInventoryName()
    {
        return "gui.title.filter_pipe.name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        host.markHostDirty();
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings, IInventory host)
    {
        SlotGroup slotGroup = new SlotGroup("transfer_node_buffer[0]", 1);
        SlotGroup slotGroup1 = new SlotGroup("transfer_node_upgrades", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(getInventoryName()))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        IItemHandler itemHandler = new InvWrapper(host);

        Flow flow = Flow.row();
        flow.pos(34,60).size(108,18);
        for (int i = 0; i < 5; i++)
        {
            flow.child(new ItemSlot().slot(new ModularSlot(itemHandler,i + 1).slotGroup(slotGroup1)));
        }
        panel.child(flow);
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(slotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        syncManager.addCloseListener(this::parseInventoryToFilter);

        return panel;
    }

    public void parseInventoryToFilter(EntityPlayer player)
    {
        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < filters.length; i++)
            {
                if (filters[i] != null)
                {
                    logicalFilter[i] = new ItemFilter();
                    if (filters[i].getItem() instanceof ItemUpgrade)
                    {
                        if (TransferUpgrade.getUpgrade(filters[i]) == TransferUpgrade.FILTER) // Case where it's a filter
                        {
                            parseFilterItem(filters[i], i);
                        }
                        else if (TransferUpgrade.getUpgrade(filters[i]) == TransferUpgrade.ADV_FILTER) // Case where it's an advanced filter
                        {
                            if (filters[i].hasTagCompound() && filters[i].stackTagCompound.getByte("Mode") == ItemUpgrade.FilterMode.INVERTED.ordinal())
                            {
                                logicalFilter[i].addToPredicates(AdvancedFilterMode.values()[filters[i].getItemDamage()]::invMatches);
                            }
                            else
                            {
                                logicalFilter[i].addToPredicates(AdvancedFilterMode.values()[filters[i].getItemDamage()]::matches);
                            }
                        }
                    }
                    else
                    {
                        logicalFilter[i].addToWhiteList(filters[i]);
                    }
                }
            }
        }
    }

    public void parseFilterItem(ItemStack filter, int slot)
    {
        ItemUpgrade filterUpgrade = (ItemUpgrade) filter.getItem();
        List<ItemStack> containedItems = getInventoryContentsFromStack(filter);

        for (ItemStack containedItem : containedItems)
        {
            if (containedItem.getItem() instanceof ItemUpgrade)
            {
                if (TransferUpgrade.getUpgrade(containedItem) == TransferUpgrade.FILTER) // Case where it's a filter
                {
                    parseFilterItem(containedItem, slot);
                }
                else if (TransferUpgrade.getUpgrade(containedItem) == TransferUpgrade.ADV_FILTER) // Case where it's an advanced filter
                {
                    if (containedItem.hasTagCompound() && containedItem.stackTagCompound.getByte("Mode") == ItemUpgrade.FilterMode.INVERTED.ordinal())
                    {
                        logicalFilter[slot].addToPredicates(AdvancedFilterMode.values()[containedItem.getItemDamage()]::invMatches);
                    }
                    else
                    {
                        logicalFilter[slot].addToPredicates(AdvancedFilterMode.values()[containedItem.getItemDamage()]::matches);
                    }
                }
            }
            else
            {
                logicalFilter[slot].addToWhiteList(containedItem);
            }
        }

    }

    @Override
    public MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(TransportType transportType, ForgeDirection incomingDirection, IWalkingComponent walkingComponent)
    {
        Object walkingObject = walkingComponent.getWalkingObject();
        if (walkingObject instanceof ItemStack stack)
        {
            int mask = 0;
            for (int i = 0; i < filters.length; i++)
            {
                if (filters[i] == null && (networkMask & (1 << i)) != 0) // If a direction is unfiltered and has a network connection.
                {
                    mask = mask | (1 << i);
                }
                else if (logicalFilter[i].matches(stack))// We're filtered
                {
                    mask = mask | (1 << i);
                }
            }
            return new MaskedArrayView<>(mask, networkNeighbors);
        }
        return super.getWalkableDirs(transportType, incomingDirection, walkingComponent);
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

}
