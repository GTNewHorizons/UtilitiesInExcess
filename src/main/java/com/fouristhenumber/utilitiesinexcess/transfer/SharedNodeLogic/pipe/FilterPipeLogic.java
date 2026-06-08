package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.NetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.TransferUpgrade;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.ItemFilter;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots.CYAN_SLOT;
import static com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots.GREEN_SLOT;
import static com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots.ORANGE_SLOT;
import static com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots.PINK_SLOT;
import static com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots.PURPLE_SLOT;
import static com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots.YELLOW_SLOT;
import static com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory.getInventoryContentsFromStack;

public class FilterPipeLogic extends NetworkLogic implements IInventory
{
    // This is the actual items in the filter.
    private final ItemStack[] filters = new ItemStack[6];

    // This is the logic that those items represent.
    private final ItemFilter[] logicalFilter = new ItemFilter[6];

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

    public void readFromNBT(NBTTagCompound compound)
    {
        NBTTagList list = compound.getTagList("Items", 10);

        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);

            int slot = itemTag.getByte("Slot") & 255;

            if (slot >= 0 && slot < this.getSizeInventory())
            {
                filters[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
        parseInventoryToFilter();
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < filters.length; i++)
        {
            ItemStack stack = filters[i];
            if (stack != null)
            {
                NBTTagCompound itemCompound = new NBTTagCompound();
                itemCompound.setByte("Slot", (byte) i);
                stack.writeToNBT(itemCompound);
                itemList.appendTag(itemCompound);
            }
        }
        compound.setTag("Items", itemList);
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
        return 1;
    }

    @Override
    public void markDirty() {
        host.markHostDirty();
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings, IInventory host)
    {
        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();
        panel.height(192);

        panel.child(
            IKey.str(StatCollector.translateToLocal(getInventoryName()))
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15)
        );

        IItemHandler itemHandler = new InvWrapper(host);

        Flow parentRow = Flow.row();
        parentRow.top(15);
        parentRow.size(18*5);

        Flow leftCol = Flow.col();
        leftCol.child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 2)).widgetTheme(PINK_SLOT.get()));
        leftCol.child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 4)).widgetTheme(ORANGE_SLOT.get()));
        leftCol.childPadding(18);
        leftCol.width(18);
        leftCol.top(18);
        parentRow.child(leftCol);

        Flow midCol = Flow.col();
        midCol.child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 1)).widgetTheme(CYAN_SLOT.get()));
        midCol.child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 0)).widgetTheme(YELLOW_SLOT.get()));
        midCol.childPadding(3*18);
        midCol.width(18);
        parentRow.child(midCol);

        Flow rightCol = Flow.col();
        rightCol.child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 3)).widgetTheme(GREEN_SLOT.get()));
        rightCol.child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 5)).widgetTheme(PURPLE_SLOT.get()));
        rightCol.childPadding(18);
        rightCol.width(18);
        rightCol.top(18);
        parentRow.child(rightCol);

        panel.child(parentRow);

        parentRow.horizontalCenter();
        parentRow.childPadding(18);
        syncManager.addCloseListener(this::closeListener);

        return panel;
    }

    public void closeListener(EntityPlayer player)
    {
        if (!player.worldObj.isRemote)
        {
            parseInventoryToFilter();
        }
    }
    public void parseInventoryToFilter()
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

    private void parseFilterItem(ItemStack filter, int slot)
    {
        List<ItemStack> containedItems = getInventoryContentsFromStack(filter);
        if (containedItems == null)
        {
            return;
        }
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
                if (incomingDirection.ordinal() != i)
                {
                    if ((networkMask & (1 << i)) != 0) // No reason to look in non-networked directions
                    {
                        if (filters[i] == null) // If a direction is unfiltered and has a network connection.
                        {
                            mask = mask | (1 << i);
                        }
                        else if (logicalFilter[i] != null && logicalFilter[i].matches(stack))// We're filtered
                        {
                            mask = mask | (1 << i);
                        }
                    }
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
