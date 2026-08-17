package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemRetrievalNode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ItemWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import com.fouristhenumber.utilitiesinexcess.utils.filter.ItemFilter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade.FilterMode.getModesFromStack;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FilterPipeLogic.parseFilterItem;
import static com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode.getAdvFilterMode;
import static com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter.canStacksMerge;

public class ItemRetrievalNodeLogic extends BaseItemTransferNodeLogic<IWalkingComponent<ItemStack>> implements IInventory
{
    public ItemWalker walker;
    ItemStack buffer;
    IInventory connectedInventory;

    // Upgrades
    private boolean isRoundRobin = false;
    private ItemFilter logicalFilter;
    private boolean init = false;

    private ItemStack pullingItem;

    public ItemRetrievalNodeLogic(IWalkingComponent<ItemStack> host) {
        super(host);
        this.walker = new ItemWalker(host);
    }

    // Weird thing to note, retrieval node walkers just get locked out of filter pipes in all directions that are filtered.
    // All other types of pipes sans energy are fine to walk through.
    // Also, retrieval pipes will reset once one type of item has been emptied from the target inventory.
    // For example if you have 50 cobble and 10 dirt. It will take out all the dirt, reset move on, then take out all
    // the cobble on the next time it finds the target inventory.
    // Retrieval nodes can pull out of more than 1 inventory at a time. If two connected inventories are accessible
    // through the same block then
    public void updateEntity()
    {
        if (host.getWorld().isRemote)
        {
            return;
        }

        if (!init)
        {
            walker.init();
            upgrades.init();
            init = true;
        }
        int actionsThisTick = actionsThisTick();
        for (int i = 0; i < actionsThisTick; i ++)
        {
            if (connectedInventory == null)
            {
                updateConnectedInventory();
            }
            else
            {
                exportToConnected();
            }

            if (buffer != null && buffer.stackSize == buffer.getMaxStackSize())
            {
                walker.reset();
                return;
            }

            List<TargetResolver.Target<IInventory>> pullingInventories = walker.getValidTargets(host.getWorld());

            if (pullingInventories.isEmpty())
            {
                walker.step(host.getWorld());
                return;
            }

            if (isRoundRobin)
            {
                importFromPullingInventories(pullingInventories);
                walker.step(host.getWorld());
            }
            else if (!importFromPullingInventories(pullingInventories))
            {
                pullingItem = null;
                walker.step(host.getWorld());
            }
        }
    }

    public boolean importFromPullingInventories(List<TargetResolver.Target<IInventory>> pullingInventories)
    {
        boolean foundTargetItem = false;
        for (TargetResolver.Target<IInventory> target : pullingInventories)
        {
            foundTargetItem = importItem(target) | foundTargetItem;
        }
        return foundTargetItem;
    }

    public boolean importItem(TargetResolver.Target<IInventory> inv)
    {
        int[] slots;

        if (inv.handler instanceof ISidedInventory sidedHandler)
        {
            slots = sidedHandler.getAccessibleSlotsFromSide(inv.side);
        }
        else
        {
            slots = new int[inv.handler.getSizeInventory()];
            for (int i = 0; i < slots.length; i++)
            {
                slots[i] = i;
            }
        }

        for (int slot : slots)
        {
            ItemStack stackInSlot = inv.handler.getStackInSlot(slot);

            if (stackInSlot == null || stackInSlot.stackSize <= 0)
            {
                continue;
            }

            if (logicalFilter != null && !logicalFilter.matches(stackInSlot))
            {
                continue;
            }

            if (pullingItem == null)
            {
                // Sometimes if for some reason you have a retrieval node that doesn't have a connected inventory
                // (or the inventory is full) you will just keep walking with a stack in the buffer.
                if (buffer != null)
                {
                    pullingItem = buffer;
                }
                else
                {
                    pullingItem = stackInSlot.copy();
                    pullingItem.stackSize = 1;
                }
            }
            else if (!canStacksMerge(pullingItem, stackInSlot)) // We want to pull all the same item out at once if possible
            {
                continue;
            }

            // Respect sided extraction rules if applicable
            if (connectedInventory instanceof ISidedInventory sided)
            {
                if (!sided.canExtractItem(slot, stackInSlot, inv.side))
                {
                    continue;
                }
            }

            int amountMoved = addToOwnInventory(stackInSlot);
            if (amountMoved == 0)
            {
                continue;
            }

            inv.handler.setInventorySlotContents(slot, stackInSlot.stackSize <= 0 ? null : stackInSlot);
            return true;
        }

        return false;
    }

    private void exportToConnected()
    {
        if (connectedInventory == null || buffer == null || buffer.stackSize <= 0)
        {
            return;
        }

        ItemStack toExport = buffer;
        ForgeDirection connectedSide = host.getFacing();

        if (connectedInventory instanceof ISidedInventory sidedInventory)
        {
            int[] slots = sidedInventory.getAccessibleSlotsFromSide(connectedSide.ordinal());
            for (int slot : slots)
            {
                if (toExport.stackSize <= 0)
                {
                    break;
                }

                if (!sidedInventory.canInsertItem(slot, toExport, connectedSide.ordinal()))
                {
                    continue;
                }

                tryMergeIntoSlot(connectedInventory, slot, toExport);
            }
        }
        else
        {
            for (int slot = 0; slot < connectedInventory.getSizeInventory(); slot++)
            {
                if (toExport.stackSize <= 0)
                {
                    break;
                }

                if (!connectedInventory.isItemValidForSlot(slot, toExport))
                {
                    continue;
                }

                tryMergeIntoSlot(connectedInventory, slot, toExport);
            }
        }

        buffer = toExport.stackSize <= 0 ? null : toExport;
    }

    private void tryMergeIntoSlot(IInventory inventory, int slot, ItemStack toExport)
    {
        ItemStack existing = inventory.getStackInSlot(slot);

        if (existing == null)
        {
            int moveCount = Math.min(toExport.stackSize, inventory.getInventoryStackLimit());
            ItemStack moved = toExport.copy();
            moved.stackSize = moveCount;
            inventory.setInventorySlotContents(slot, moved);
            toExport.stackSize -= moveCount;
        }
        else if (canStacksMerge(existing, toExport))
        {
            int space = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit()) - existing.stackSize;

            if (space <= 0)
            {
                return;
            }

            int moveCount = Math.min(space, toExport.stackSize);
            existing.stackSize += moveCount;
            toExport.stackSize -= moveCount;
            inventory.setInventorySlotContents(slot, existing);
        }
    }

    public void updateConnectedInventory()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IInventory inventory) {
            connectedInventory = inventory;
        }
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return buffer;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackInventory.decrStackSize(count, this);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return buffer;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.buffer = stack;
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "";
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

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    // ======================================= Upgrades =======================================
    // Applicable upgrades: Speed, Stack , BFS, DFS, RoundRobin, Filter, Adv Filter
    @Override
    public void resetUpgrades()
    {
        super.resetUpgrades();
        this.walker.setStepper(new RandomStepper());
        this.isStackUpgrade = false;
        this.logicalFilter = null;
        this.isRoundRobin = false;
    }

    @Override
    public void applySearchDepthUpgrade(ItemStack stack)
    {
        this.walker.setStepper(new DFSStepper());
    }

    @Override
    public void applySearchBreadthUpgrade(ItemStack stack)
    {
        this.walker.setStepper(new BFSStepper());
    }

    @Override
    public void applySearchRoundRobinUpgrade(ItemStack stack)
    {
        this.isRoundRobin = true;
    }

    @Override
    public void applyStackUpgrade(ItemStack stack)
    {
        this.isStackUpgrade = true;
    }

    @Override
    public void applyFilterUpgrade(ItemStack filter)
    {
        if (logicalFilter == null)
        {
            logicalFilter = new ItemFilter();
        }
        parseFilterItem(logicalFilter, filter, getModesFromStack(filter));
    }

    @Override
    public void applyAdvFilterUpgrade(ItemStack advFilter)
    {
        if (logicalFilter == null)
        {
            logicalFilter = new ItemFilter();
        }
        logicalFilter.addToPredicates(
            AdvancedFilterMode.values()[getAdvFilterMode(advFilter)]::matches,
            ItemUpgrade.FilterMode.isInverted(advFilter)
        );
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup bufferSlotGroup = new SlotGroup("retrieval_node_buffer", 1);
        SlotGroup upgradeSlotGroup = new SlotGroup("retrieval_node_upgrades", 1);

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


        panel.child(
            IKey.dynamic(() -> "Search Location: " + searchLocationSyncer.getStringValue())
                .asWidget()
                .marginTop(20)
                .horizontalCenter()
        );

        Flow flow = Flow.row();
        flow.pos(34,60).size(108,18);
        // Upgrades
        IItemHandler upgradeItemHandler = new InvWrapper(upgrades);

        for (int i = 0; i < upgrades.getSizeInventory(); i++)
        {
            flow.child(new ItemSlot().slot(new ModularSlot(upgradeItemHandler,i).slotGroup(upgradeSlotGroup).changeListener(upgrades)));
        }

        panel.child(flow);
        // Buffer
        IItemHandler bufferItemHandler = new InvWrapper(this);
        ModularSlot slot = new ModularSlot(bufferItemHandler, 0).slotGroup(bufferSlotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }

}
