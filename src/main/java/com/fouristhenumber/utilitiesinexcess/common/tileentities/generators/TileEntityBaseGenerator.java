package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import net.minecraft.inventory.IInventory;
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
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public abstract class TileEntityBaseGenerator extends TileEntity
    implements IEnergyProvider, IInventory, IGuiHolder<PosGuiData> {

    protected EnergyStorage energyStorage;
    protected int burnTime;
    protected int currentFuelBurnTime;
    protected int currentRFPerTick;
    protected boolean isBurning;

    protected IEnergyReceiver[] connectedReceivers = new IEnergyReceiver[6];
    protected boolean receiversDirty = false;

    public static final UITexture ENERGY_PROGRESS = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/progress_energy")
        .adaptable(1)
        .imageSize(16, 128)
        .build();

    // Generators can "receive" infinite energy because this allows them to generate however much they say.
    // Nothing can push to them anyway.
    public TileEntityBaseGenerator(int capacity, int maxTransfer) {
        this.energyStorage = new EnergyStorage(capacity, Integer.MAX_VALUE, maxTransfer);
    }

    /**
     * Override to set fuel consumption behavior. Return true if fuel consumption succeeds.
     * A proper implementation of consumeFuel should usually set currentFuelBurnTime and currentRFPerTick!
     */
    protected abstract boolean consumeFuel();

    /**
     * Called every burn tick.
     */
    protected void onBurnTick() {}

    @Override
    public void updateEntity() {
        boolean dirty = false;

        if (receiversDirty) refreshEnergyReceivers();

        if (burnTime > 0) {
            burnTime--;
            energyStorage.receiveEnergy(currentRFPerTick, false);
            isBurning = true;
            dirty = true;
            onBurnTick();
        } else {
            isBurning = false;
            if (consumeFuel()) {
                burnTime = currentFuelBurnTime;
                dirty = true;
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
        currentFuelBurnTime = tag.getInteger("currentFuelBurnTime");
        currentRFPerTick = tag.getInteger("CurrentRFPerTick");
        energyStorage.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("BurnTime", burnTime);
        tag.setInteger("currentFuelBurnTime", currentFuelBurnTime);
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
                            formatNumber(energySyncer.getIntValue()),
                            formatNumber(maxEnergySyncer.getIntValue())))));

        return panel;
    }
}
