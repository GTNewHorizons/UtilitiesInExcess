package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

// This class doesn't really even need to be a IInventory because of composition on FluidTank + UpgradeInventory.
// I think it's just simpler design.
public class FluidRetrievalNodeLogic extends BaseFluidTransferNodeLogic
{
    IFluidHandler connectedTank;

    // Upgrades
    private boolean isRoundRobin = false;

    public FluidRetrievalNodeLogic(IWalkingComponent host)
    {
        super(host);
        this.walker = new FluidWalker(host);
    }

    // ======================================= Ticking =======================================
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
            updateConnectedTank();
            if (connectedTank != null)
            {
                exportToConnected();
            }

            if (buffer.getFluid() != null && buffer.getFluid().amount == maxFluidAmount) {
                walker.reset();
                return;
            }

            List<TargetResolver.Target<IFluidHandler>> pullingTanks = walker.getValidTargets(host.getWorld());

            if (pullingTanks.isEmpty()) {
                walker.step(host.getWorld());
                return;
            }

            // In roundrobin we always step after an action
            if (isRoundRobin)
            {
                importFromPullingTanks(pullingTanks);
                walker.step(host.getWorld());
            }
            else if (!importFromPullingTanks(pullingTanks))
            {
                walker.step(host.getWorld());
            }
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
                    host.markHostDirty();
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
                        host.markHostDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateConnectedTank()
    {
        ForgeDirection facing = ((IWalkingComponent) host).getFacing();
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
        ForgeDirection connectedSide = ((IWalkingComponent) host).getFacing().getOpposite();
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
            host.markHostDirty();
        }
    }

    // ======================================= Upgrades =======================================
    // Applicable upgrades: Creative, Speed, Stack, BFS, DFS, RoundRobin
    @Override
    public void resetUpgrades()
    {
        super.resetUpgrades();
        this.walker.setStepper(new RandomStepper());
        this.isRoundRobin = false;
        this.maxDrainAmount = DEFAULT_MAX_DRAIN_AMOUNT;
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
        this.maxDrainAmount = maxFluidAmount;
    }

    public String getInventoryName()
    {
        return "uie.gui.title.fluid_retrieval_node.name";
    }
}
