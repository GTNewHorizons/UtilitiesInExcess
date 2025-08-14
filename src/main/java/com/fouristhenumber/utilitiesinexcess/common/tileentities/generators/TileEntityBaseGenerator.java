package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public abstract class TileEntityBaseGenerator extends TileEntity
    implements IEnergyProvider, IInventory, IGuiHolder<PosGuiData> {

    protected EnergyStorage energyStorage;
    protected int burnTime;
    protected int currentItemBurnTime;
    protected int currentRFPerTick;
    protected boolean isBurning;

    protected IEnergyReceiver[] connectedReceivers = new IEnergyReceiver[6];
    protected boolean receiversDirty = false;

    public static final UITexture ENERGY_PROGRESS = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/progress_energy")
        .adaptable(1)
        .imageSize(16, 128)
        .build();

    public TileEntityBaseGenerator(int capacity, int maxTransfer) {
        this.energyStorage = new EnergyStorage(capacity, maxTransfer);
    }

    /** Override to define how much RF per tick a given fuel stack provides. */
    protected abstract int getRFPerTick(ItemStack stack);

    /** Override to define burn time for a given fuel stack. */
    protected abstract int getFuelBurnTime(ItemStack stack);

    /** Override to consume fuel (usually decrement stack size in inventory). */
    protected abstract ItemStack consumeFuel();

    @Override
    public void updateEntity() {
        boolean dirty = false;

        if (receiversDirty) refreshEnergyReceivers();

        if (burnTime > 0) {
            burnTime--;
            energyStorage.receiveEnergy(currentRFPerTick, false);
            isBurning = true;
            dirty = true;
        } else {
            isBurning = false;
            ItemStack fuel = getFuelStack();
            if (fuel != null) {
                int time = getFuelBurnTime(fuel);
                currentRFPerTick = getRFPerTick(fuel);
                if (time > 0) {
                    currentItemBurnTime = burnTime = time;
                    consumeFuel();
                    dirty = true;
                }
            }
        }

        pushEnergyToReceivers();

        if (dirty) {
            markDirty();
        }
    }

    protected void pushEnergyToReceivers() {
        if (energyStorage.getEnergyStored() <= 0) return;

        int canSend = energyStorage.getMaxExtract();

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IEnergyReceiver receiver = connectedReceivers[dir.ordinal()];
            if (receiver != null) {
                int toSend = Math.min(canSend, energyStorage.getEnergyStored());
                int received = receiver.receiveEnergy(dir.getOpposite(), toSend, false);
                if (received > 0) {
                    canSend -= received;
                    energyStorage.extractEnergy(received, false);
                    if (energyStorage.getEnergyStored() <= 0 || canSend <= 0) {
                        break;
                    }
                }
            }
        }
    }

    /** Override to return the stack representing current fuel in inventory. */
    protected abstract ItemStack getFuelStack();

    public void onNeighborBlockChange() {
        receiversDirty = true;
    }

    @Override
    public void validate() {
        super.validate();
        receiversDirty = true;
    }

    protected void refreshEnergyReceivers() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
            if (te instanceof IEnergyReceiver rec) {
                if (rec.canConnectEnergy(dir.getOpposite())) {
                    connectedReceivers[dir.ordinal()] = rec;
                } else {
                    connectedReceivers[dir.ordinal()] = null;
                }
            } else {
                connectedReceivers[dir.ordinal()] = null;
            }
        }
        receiversDirty = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        burnTime = tag.getInteger("BurnTime");
        currentItemBurnTime = tag.getInteger("CurrentItemBurnTime");
        currentRFPerTick = tag.getInteger("CurrentRFPerTick");
        energyStorage.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("BurnTime", burnTime);
        tag.setInteger("CurrentItemBurnTime", currentItemBurnTime);
        tag.setInteger("CurrentRFPerTick", currentRFPerTick);
        energyStorage.writeToNBT(tag);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        IntSyncValue energySyncer = new IntSyncValue(() -> energyStorage.getEnergyStored(), ignore -> {});
        IntSyncValue maxEnergySyncer = new IntSyncValue(() -> energyStorage.getMaxEnergyStored(), ignore -> {});
        syncManager.syncValue("energySyncer", energySyncer);
        syncManager.syncValue("maxEnergySyncer", maxEnergySyncer);

        SlotGroup slotGroup = new SlotGroup("fuel_slot", 1);

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
        panel.child(
            new ProgressWidget()
                .value(
                    new DoubleSyncValue(
                        () -> (double) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored()))
                .texture(ENERGY_PROGRESS, 16)
                .direction(ProgressWidget.Direction.UP)
                .size(16, 64)
                .pos(100, 14)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "gui.energy.tooltip",
                            energySyncer.getStringValue(),
                            maxEnergySyncer.getStringValue()))));

        return panel;
    }
}
