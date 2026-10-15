package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.transfer.gui.NodeGui;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ItemWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.WalkerBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter.canStacksMerge;
import static com.fouristhenumber.utilitiesinexcess.utils.InventoryUtils.dropStack;
import static com.fouristhenumber.utilitiesinexcess.utils.InventoryUtils.getInventory;

public abstract class BaseItemTransferNodeLogic extends BaseNodeLogic implements IInventory
{
    protected ItemStack buffer;
    protected boolean isStackUpgrade = false;
    public ItemWalker walker;
    protected IInventory connectedInventory;

    public BaseItemTransferNodeLogic(IWalkingComponent host) {
        super(host);
    }

    @Override
    public void invalidateInventory()
    {
        super.invalidateInventory();
        buffer = null;
    }

    @Override
    public WalkerBase getWalker()
    {
        return walker;
    }

    @Override
    public void initFromOtherLogic(NetworkLogic logic)
    {
        super.initFromOtherLogic(logic);
        if (logic instanceof BaseItemTransferNodeLogic itemNodeLogic)
        {
            this.buffer = itemNodeLogic.buffer;
        }
    }

    protected int addToOwnInventory(ItemStack sourceStack)
    {
        ItemStack existing = this.getStackInSlot(0);

        // empty slot
        if (existing == null)
        {
            ItemStack copy = sourceStack.copy();

            if (!this.isStackUpgrade)
            {
                copy.stackSize = 1;
            }

            this.setInventorySlotContents(0, copy);
            sourceStack.stackSize -= copy.stackSize;
            return copy.stackSize;
        }

        // can merge
        if (canStacksMerge(existing, sourceStack))
        {
            int space = existing.getMaxStackSize() - existing.stackSize;

            if (space <= 0)
                return 0;

            int toMove = this.isStackUpgrade ? Math.min(space, sourceStack.stackSize) : 1;

            existing.stackSize += toMove;
            this.setInventorySlotContents(0, existing);

            // shrink source
            sourceStack.stackSize -= toMove;

            return toMove;
        }

        return 0;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.buffer = stack;
        this.markDirty();
    }

    @Override
    public ItemStack getWalkingObject()
    {
        return buffer;
    }

    @Override
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

    @Override
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
    public void writeDesc(MCDataOutput output)
    {
        super.writeDesc(output);
        output.writeItemStack(buffer);

    }

    @Override
    public void readDesc(MCDataInput input)
    {
        super.readDesc(input);
        buffer = input.readItemStack();
    }

    @Override
    public void dropContents(World world, int x, int y, int z)
    {
        super.dropContents(world, x, y, z);
        if (buffer == null || buffer.stackSize <= 0)
        {
            return;
        }
        dropStack(world, x, y, z, buffer);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> "Search Location: " + walker.getLocationString());
        SlotGroup bufferSlotGroup = new SlotGroup("transfer_node_buffer", 1);
        IItemHandler bufferItemHandler = new InvWrapper(this);

        return NodeGui.buildUI(upgrades, "transfer_node_upgrades", getInventoryName(), searchLocationSyncer,
            () -> new ItemSlot().slot(
                new ModularSlot(bufferItemHandler, 0)
                    .slotGroup(bufferSlotGroup)), syncManager
        );
    }

    public void updateSourceInventory()
    {
        ForgeDirection facing = ((IWalkingComponent) host).getFacing();
        connectedInventory = getInventory(host.getWorld(), host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
    }
}
