package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ItemWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import com.fouristhenumber.utilitiesinexcess.utils.filter.ItemFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade.FilterMode.getModesFromStack;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FilterPipeLogic.parseFilterItem;
import static com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode.getAdvFilterMode;

public class ItemTransferNodeLogic extends BaseItemTransferNodeLogic
{
    // Upgrades
    private boolean isCreative = false;
    private ItemFilter logicalFilter;
    private boolean isRoundRobin = false;

    public ItemTransferNodeLogic(IWalkingComponent host)
    {
        super(host);
        this.walker = new ItemWalker(host);
    }

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
        for (int i = 0; i < actionsThisTick; i ++) {
            updateSourceInventory();
            if (connectedInventory != null) {
                importItems();
            }

            if (buffer != null)
            {
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
                            host.markHostDirty();
                        }
                    }
                }
            }

            if (buffer == null)
            {
                if (!this.isRoundRobin)
                {
                    walker.reset();
                }

                return;
            }

            walker.step(host.getWorld());
        }
    }

    public void importItems()
    {
        ForgeDirection connectedSide = ((IWalkingComponent) host).getFacing().getOpposite();
        int[] slots;

        if (connectedInventory instanceof ISidedInventory sided)
        {
            slots = sided.getAccessibleSlotsFromSide(connectedSide.ordinal());
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
                if (!sided.canExtractItem(slot, stackInSlot, connectedSide.ordinal()))
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
    public String getInventoryName()
    {
        return "uie.gui.title.item_transfer_node.name";
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
        parseFilterItem(logicalFilter, filter, 0);
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
}
