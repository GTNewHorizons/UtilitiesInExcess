package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;

public class TileEntityTransferNodeFluid extends TileEntityTransferNodeBase implements IGuiHolder<PosGuiData> {

    FluidTank buffer = new FluidTank(8000);
    IFluidHandler connectedInventory;

    @Override
    public boolean canConnectTo(World world, int x, int y, int z, int side) {
        if (side == blockMetadata) return false;

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof ITransferNetworkComponent transfer) {
            return transfer.canConnectFrom(ForgeDirection.getOrientation(side));
        }

        return te instanceof IFluidHandler;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote || this.worldObj.getTotalWorldTime() % 20 != 0) return;
        if (buffer != null && buffer.getFluidAmount() >= buffer.getCapacity()) return;
        if (connectedInventory == null) {
            ForgeDirection facing = ForgeDirection.getOrientation(getBlockMetadata());
            TileEntity neighbor = worldObj
                .getTileEntity(xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ);
            if (neighbor instanceof IFluidHandler inventory) {
                connectedInventory = inventory;
            }
        }
        if (connectedInventory != null) importFluids();
    }

    public void importFluids() {
        FluidStack canDrain = connectedInventory.drain(ForgeDirection.UP, 200, false);
        if (canDrain != null && canDrain.amount > 0) {
            int filled = buffer.fill(canDrain, true);
            if (filled > 0) {
                connectedInventory.drain(ForgeDirection.UP, filled, true);
            }
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal("gui.title.transfer_node_fluid.name"))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        FluidSlotSyncHandler fluidSync = new FluidSlotSyncHandler(buffer);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new FluidSlot().syncHandler(fluidSync)));

        return panel;
    }
}
