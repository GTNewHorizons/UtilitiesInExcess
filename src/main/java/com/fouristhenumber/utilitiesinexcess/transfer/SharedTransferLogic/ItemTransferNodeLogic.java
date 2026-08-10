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
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ItemWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RoundRobinStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import com.fouristhenumber.utilitiesinexcess.utils.filter.ItemFilter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade.FilterMode.getModesFromStack;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FilterPipeLogic.parseFilterItem;
import static com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode.getAdvFilterMode;
import static com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter.canStacksMerge;
import static com.fouristhenumber.utilitiesinexcess.utils.InventoryUtils.getInventory;

public class ItemTransferNodeLogic extends BaseTransferNodeLogic<TileEntityItemTransferNode> implements IInventory
{
    IInventory connectedInventory;
    public ItemWalker walker;

    // Upgrades
    private boolean isCreative = false;
    private ItemFilter logicalFilter;
    private boolean init = false;
    private boolean isRoundRobin = false;

    public ItemTransferNodeLogic(TileEntityItemTransferNode host)
    {
        super(host);
        this.walker = new ItemWalker(host);
    }

    // Perhaps the best way to do this is creating a inserter logic that deals with insertion?
    // You can call walker.getInserter() then do inserter.insert()?

    // Note that I did write quite lengthy insertion logic for this. I felt that it is more important to keep the
    // logic concise and fast for the cases where there is no rationing pipe.
    public void updateEntity()
    {
        if (host.getWorld().isRemote)
        {
            return;
        }

        if (!init)
        {
            upgrades.init();
            init = true;
        }

        int actionsThisTick = actionsThisTick();
        for (int i = 0; i < actionsThisTick; i ++) {
            if (connectedInventory == null) {
                updateSourceInventory();
            }

            if (connectedInventory != null) {
                importItems();
            }

            if (buffer == null)
            {
                if (!this.isRoundRobin)
                {
                    walker.reset();
                }

                return;
            }

            List<TargetResolver.Target<IInventory>> targets = walker.getValidTargets(host.getWorld());
            if (!targets.isEmpty()) {
                BaseInserter inserter = walker.getInserter(host.getWorld());
                for (TargetResolver.Target<IInventory> target : targets) // Need to loop through because sometimes the full stack cannot fit in one inventory
                {
                    if (this.isCreative)
                    {
                        ItemStack creativeStack = buffer.copy();
                        inserter.insert(target, buffer);
                        buffer = creativeStack;
                    }
                    else
                    {
                        buffer = inserter.insert(target, buffer);
                    }
                }
            }
            walker.step(host.getWorld());
        }
    }

    public void importItems()
    {
        ForgeDirection facing = host.getFacing();
        int[] slots;

        if (connectedInventory instanceof ISidedInventory sided)
        {
            slots = sided.getAccessibleSlotsFromSide(facing.ordinal());
        }
        else
        {
            slots = new int[connectedInventory.getSizeInventory()];
            for (int i = 0; i < slots.length; i++)
            {
                slots[i] = i;
            }
        }

        for (int slot : slots)
        {
            ItemStack stackInSlot = connectedInventory.getStackInSlot(slot);
            if (stackInSlot == null || stackInSlot.stackSize <= 0)
            {
                continue;
            }

            if (logicalFilter != null && !logicalFilter.matches(stackInSlot))
            {
                continue;
            }

            if (connectedInventory instanceof ISidedInventory sided)
            {
                if (!sided.canExtractItem(slot, stackInSlot, facing.ordinal()))
                {
                    continue;
                }
            }

            int amountMoved = addToOwnInventory(stackInSlot);
            if (amountMoved == 0)
            {
                continue;
            }

            connectedInventory.setInventorySlotContents(slot, stackInSlot.stackSize <= 0 ? null : stackInSlot);
            break;
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (buffer != null)
        {
            NBTTagCompound compound = new NBTTagCompound();
            this.buffer.writeToNBT(compound);
            nbt.setTag("Buffer", compound);
        }
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Buffer"))
        {
            NBTTagCompound compound = nbt.getCompoundTag("Buffer");
            this.buffer = ItemStack.loadItemStackFromNBT(compound);
        }
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn)
    {
        return buffer;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackInventory.decrStackSize(count, this);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return buffer;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        buffer = stack;
        this.markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "gui.title.item_transfer_node.name";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty() {
        host.markHostDirty();
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

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    // ======================================= Upgrades =======================================
    // Applicable upgrades: Creative, Speed, Stack , BFS, DFS, RoundRobin, Filter, Adv Filter
    @Override
    public void resetUpgrades()
    {
        super.resetUpgrades();
        this.walker.setStepper(new RandomStepper());
        this.isCreative = false;
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
        this.walker.setStepper(new RandomStepper());
        this.isRoundRobin = true;
    }

    @Override
    public void applyCreativeUpgrade(ItemStack stack)
    {
        this.isCreative = true;
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

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup bufferSlotGroup = new SlotGroup("transfer_node_buffer", 1);
        SlotGroup upgradeSlotGroup = new SlotGroup("transfer_node_upgrades", 1);

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

        // upgrades
        IItemHandler upgradeItemHandler = new InvWrapper(upgrades);
        for (int i = 0; i < upgrades.getSizeInventory(); i++) // First slot is for buffer
        {
            flow.child(new ItemSlot().slot(new ModularSlot(upgradeItemHandler,i).slotGroup(upgradeSlotGroup).changeListener(upgrades)));
        }

        // buffer
        IItemHandler bufferItemHandler = new InvWrapper(this);
        panel.child(flow);
        ModularSlot slot = new ModularSlot(bufferItemHandler, 0).slotGroup(bufferSlotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }

    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    public void updateSourceInventory()
    {
        ForgeDirection facing = host.getFacing();
        connectedInventory = getInventory(host.getWorld(), host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
    }
}
