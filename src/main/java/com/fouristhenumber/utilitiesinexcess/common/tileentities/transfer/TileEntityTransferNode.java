package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import cofh.api.energy.IEnergyReceiver;

public class TileEntityTransferNode extends TileEntity
    implements ITransferNetworkComponent, ISidedInventory, IGuiHolder<PosGuiData> {

    ItemStack buffer;
    IInventory connectedInventory;

    private int connectionsMask = 0;

    public int getConnectionsMask() {
        return connectionsMask;
    }

    public boolean updateConnections(World world, int x, int y, int z) {
        int old = connectionsMask;
        int mask = 0;
        if (canConnectTo(world, x + 1, y, z, 5)) mask |= 1;
        if (canConnectTo(world, x - 1, y, z, 4)) mask |= 1 << 1;
        if (canConnectTo(world, x, y + 1, z, 1)) mask |= 1 << 2;
        if (canConnectTo(world, x, y - 1, z, 0)) mask |= 1 << 3;
        if (canConnectTo(world, x, y, z + 1, 3)) mask |= 1 << 4;
        if (canConnectTo(world, x, y, z - 1, 2)) mask |= 1 << 5;

        connectionsMask = mask;
        if (old != mask) {
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean canConnectTo(World world, int x, int y, int z, int side) {
        if (side == blockMetadata) return false;

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof ITransferNetworkComponent transfer) {
            return transfer.canConnectFrom(ForgeDirection.getOrientation(side));
        }

        if (te instanceof ISidedInventory sided) {
            int[] slots = sided.getAccessibleSlotsFromSide(side);
            if (slots != null && slots.length > 0) return true;
        } else if (te instanceof IInventory) return true;

        if (te instanceof IEnergyReceiver energy && energy.canConnectEnergy(ForgeDirection.getOrientation(side))) {
            return true;
        }

        return te instanceof IFluidHandler;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("connectionsMask", connectionsMask);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        connectionsMask = nbt.getInteger("connectionsMask");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote || this.worldObj.getTotalWorldTime() % 20 != 0) return;
        if (buffer != null && buffer.stackSize >= 64) return;
        if (connectedInventory == null) {
            ForgeDirection facing = ForgeDirection.getOrientation(getBlockMetadata());
            TileEntity neighbor = worldObj
                .getTileEntity(xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ);
            if (neighbor instanceof IInventory inventory) {
                connectedInventory = inventory;
            }
        }
        if (connectedInventory != null) importItems();
    }

    public void importItems() {
        for (int slot = 0; slot < connectedInventory.getSizeInventory(); slot++) {
            ItemStack stackInSlot = connectedInventory.getStackInSlot(slot);
            if (stackInSlot != null) {
                if (buffer == null) {
                    buffer = stackInSlot.splitStack(1);
                    break;
                } else if (buffer.isItemEqual(stackInSlot)) {
                    stackInSlot.splitStack(1);
                    buffer.stackSize += 1;
                    if (stackInSlot.stackSize <= 0) {
                        connectedInventory.setInventorySlotContents(slot, null);
                    }
                    break;
                }
            }
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
        if (buffer == null) return null;
        return buffer.splitStack(count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return buffer;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        buffer = stack;
    }

    @Override
    public String getInventoryName() {
        return "tile.transfer_node.name";
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        SlotGroup slotGroup = new SlotGroup("transfer_node_buffer", 1);

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

        IItemHandler itemHandler = new InvWrapper(this);
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(slotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (ForgeDirection.getOrientation(side)
            .getOpposite()
            .ordinal() == blockMetadata) return null;
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return side != blockMetadata;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canConnectFrom(ForgeDirection side) {
        return side.getOpposite()
            .ordinal() != blockMetadata;
    }
}
