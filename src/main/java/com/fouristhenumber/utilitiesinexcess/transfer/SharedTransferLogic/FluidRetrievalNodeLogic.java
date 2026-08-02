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
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidRetrievalNode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.IUpgradeable;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.UpgradeInventory;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class FluidRetrievalNodeLogic extends NetworkLogic<TileEntityFluidRetrievalNode> implements IUpgradeable
{
//    ItemStack[] upgrades = new ItemStack[getSizeInventory()];
    public static final int maxFluidAmount = 8000;
    public int maxDrainAmount = 200;
    public FluidTank buffer = new FluidTank(maxFluidAmount);
    public FluidWalker walker;
    IFluidHandler connectedTank;

    UpgradeInventory upgrades;

    // TODO?
    private FluidStack pullingFluid;

    public FluidRetrievalNodeLogic(TileEntityFluidRetrievalNode host)
    {
        super(host);
        this.walker = new FluidWalker(host);
        this.upgrades = new UpgradeInventory(6, this);
    }

    public void updateEntity()
    {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        if (connectedTank == null)
        {
            updateConnectedTank();
        }
        else
        {
            exportToConnected();
        }

        if (buffer.getFluid() != null && buffer.getFluid().amount == maxFluidAmount)
        {
            walker.reset();
            return;
        }

        List<TargetResolver.Target<IFluidHandler>> pullingTanks = walker.getValidTargets(host.getWorld());

        if (pullingTanks.isEmpty())
        {
            walker.step(host.getWorld());
            return;
        }

        if (!importFromPullingTanks(pullingTanks))
        {
            pullingFluid = null;
            walker.step(host.getWorld());
        }
    }

    public boolean importFromPullingTanks(List<TargetResolver.Target<IFluidHandler>> pullingTanks)
    {
        boolean foundTargetFluid = false;
        for (TargetResolver.Target<IFluidHandler> tank : pullingTanks)
        {
            foundTargetFluid = importFluid(tank) | foundTargetFluid;
        }
        return foundTargetFluid;
    }


    public boolean importFluid(TargetResolver.Target<IFluidHandler> tank)
    {
        ForgeDirection fromDir = ForgeDirection.getOrientation(tank.side);

        int spaceRemaining = buffer.getCapacity() - buffer.getFluidAmount();
        if (spaceRemaining <= 0)
        {
            return false;
        }

        int drainAmount = Math.min(spaceRemaining, maxDrainAmount);

        FluidStack bufferedFluid = buffer.getFluid();

        if (bufferedFluid == null)
        {
            FluidStack drainableFluid = tank.handler.drain(fromDir, drainAmount, false);
            if (drainableFluid != null && drainableFluid.amount > 0)
            {
                FluidStack drained = tank.handler.drain(fromDir, drainableFluid.amount, true);
                if (drained != null)
                {
                    buffer.fill(drained, true);
                    return true;
                }
            }
        }
        else
        {
            if (tank.handler.canDrain(fromDir, bufferedFluid.getFluid()))
            {
                FluidStack request = new FluidStack(bufferedFluid.getFluid(), drainAmount);
                FluidStack drainableFluid = tank.handler.drain(fromDir, request, false);
                if (drainableFluid != null && drainableFluid.amount > 0)
                {
                    FluidStack drained = tank.handler.drain(fromDir, new FluidStack(bufferedFluid.getFluid(), drainableFluid.amount), true);
                    if (drained != null)
                    {
                        buffer.fill(drained, true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateConnectedTank()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IFluidHandler tank)
        {
            connectedTank = tank;
        }
    }

    public void exportToConnected()
    {
        if (connectedTank == null)
        {
            return;
        }
        ForgeDirection connectedSide = host.getFacing().getOpposite();
        FluidStack available = buffer.getFluid();

        if (available == null || available.amount <= 0)
        {
            return;
        }

        if (!connectedTank.canFill(connectedSide, available.getFluid()))
        {
            return;
        }

        FluidStack toExport = available.copy();

        int filled = connectedTank.fill(connectedSide, toExport, true);

        if (filled > 0)
        {
            buffer.drain(filled, true);
        }
    }

    @Override
    public void resetUpgrades()
    {

    }

    @Override
    public void markDirty() {
        host.markDirty();
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList itemTagList = new NBTTagList();

        this.upgrades.writeToNBT(nbt);

        nbt.setTag("Items", itemTagList);

        NBTTagCompound fluidTag = new NBTTagCompound();
        buffer.writeToNBT(fluidTag);
        nbt.setTag("Fluid", fluidTag);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        upgrades.readFromNBT(nbt);

        buffer.readFromNBT(nbt.getCompoundTag("Fluid"));
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup upgradeSlotGroup = new SlotGroup("fluid_retrieval_node_upgrades", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(""))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        IItemHandler itemHandler = new InvWrapper(upgrades);

        panel.child(
            IKey.dynamic(() -> "Search Location: " + searchLocationSyncer.getStringValue())
                .asWidget()
                .marginTop(20)
                .horizontalCenter()
        );

        Flow flow = Flow.row();
        flow.pos(34,60).size(108,18);
        for (int i = 0; i < upgrades.getSizeInventory(); i++)
        {
            flow.child(new ItemSlot().slot(new ModularSlot(itemHandler,i).slotGroup(upgradeSlotGroup).changeListener(upgrades)));
        }
        panel.child(flow);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new FluidSlot().syncHandler(buffer)));

        return panel;
    }

    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }


}
