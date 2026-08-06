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
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RoundRobinStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class FluidTransferNodeLogic extends BaseNodeLogic<TileEntityFluidTransferNode>
{
    public static final int DEFAULT_MAX_DRAIN_AMOUNT = 200;
    public static final int maxFluidAmount = 8000;
    public int maxDrainAmount = DEFAULT_MAX_DRAIN_AMOUNT;
    public FluidTank buffer = new FluidTank(maxFluidAmount);


    // Upgrades
    private boolean isCreative = false;
    private boolean isWorldInteraction = false;

    IFluidHandler connectedTank;
    public FluidWalker walker;

    public FluidTransferNodeLogic(TileEntityFluidTransferNode host) {
        super(host);
        walker = new FluidWalker(host);
    }

    public void updateEntity()
    {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        if (connectedTank == null)
        {
            updateSourceTank();
        }

        if (connectedTank != null)
        {
            importFluids();
        }

        if (buffer.getFluid() == null)
        {
            walker.reset();
            return;
        }

        List<TargetResolver.Target<IFluidHandler>> targets = walker.getValidTargets(host.getWorld());
        if (!targets.isEmpty())
        {
            FluidStack fluid = buffer.getFluid();
            if (fluid != null)
            {
                FluidStack toInsert = fluid.copy();
                toInsert.amount = fluid.amount;

                for (TargetResolver.Target<IFluidHandler> target : targets)
                {
                    int filled;

                    filled = target.handler.fill(
                        ForgeDirection.getOrientation(target.side).getOpposite(),
                        toInsert,
                        true
                    );

                    toInsert.amount -= filled;

                    if (toInsert.amount <= 0)
                    {
                        break;
                    }
                }

                int inserted = fluid.amount - toInsert.amount;

                if (inserted > 0)
                {
                    buffer.drain(inserted, true);
                }
            }
        }
        walker.step(host.getWorld());
    }

    public void importFluids()
    {
        ForgeDirection fromDir = host.getFacing().getOpposite();

        int spaceRemaining = buffer.getCapacity() - buffer.getFluidAmount();
        if (spaceRemaining <= 0) return;

        int drainAmount = Math.min(spaceRemaining, maxDrainAmount);

        FluidStack bufferedFluid = buffer.getFluid();

        if (bufferedFluid == null)
        {
            FluidStack drainableFluid = connectedTank.drain(fromDir, drainAmount, false);
            if (drainableFluid != null && drainableFluid.amount > 0)
            {
                FluidStack drained = connectedTank.drain(fromDir, drainableFluid.amount, true);
                if (drained != null)
                {
                    buffer.fill(drained, true);
                }
            }
        }
        else
        {
            if (connectedTank.canDrain(fromDir, bufferedFluid.getFluid()))
            {
                FluidStack request = new FluidStack(bufferedFluid.getFluid(), drainAmount);
                FluidStack drainableFluid = connectedTank.drain(fromDir, request, false);
                if (drainableFluid != null && drainableFluid.amount > 0)
                {
                    FluidStack drained = connectedTank.drain(fromDir, new FluidStack(bufferedFluid.getFluid(), drainableFluid.amount), true);
                    if (drained != null)
                    {
                        buffer.fill(drained, true);
                    }
                }
            }
        }
    }


    public void updateSourceTank()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IFluidHandler tank)
        {
            connectedTank = tank;
        }
    }

    // ======================================= Upgrades =======================================
    // Applicable upgrades: Creative, Speed, Stack, BFS, DFS, RoundRobin, World interaction
    @Override
    public void resetUpgrades()
    {
        super.resetUpgrades();
        this.walker.setStepper(new RandomStepper());
        this.isCreative = false;
        this.maxDrainAmount = DEFAULT_MAX_DRAIN_AMOUNT;
        this.isWorldInteraction = false;
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
        this.walker.setStepper(new RoundRobinStepper());
    }

    @Override
    public void applyCreativeUpgrade(ItemStack stack)
    {
        this.isCreative = true;
    }

    @Override
    public void applyStackUpgrade(ItemStack stack)
    {
        this.maxDrainAmount = maxFluidAmount;
    }

    @Override
    public void applyWorldInteractionUpgrade(ItemStack stack)
    {
        this.isWorldInteraction = true;
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        NBTTagCompound fluidTag = new NBTTagCompound();
        buffer.writeToNBT(fluidTag);
        nbt.setTag("Fluid", fluidTag);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        buffer.readFromNBT(nbt.getCompoundTag("Fluid"));
    }

    // Note that because of the power of the fluid slot players can now put fluids in manually too!
    // I see this as a win and doesn't break forward compat in any way.
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup upgradeSlotGroup = new SlotGroup("fluid_transfer_node_upgrades", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(upgrades.getInventoryName()))
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
