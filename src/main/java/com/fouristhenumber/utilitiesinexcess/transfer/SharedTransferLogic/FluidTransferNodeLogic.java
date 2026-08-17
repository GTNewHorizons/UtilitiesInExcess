package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
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
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class FluidTransferNodeLogic extends BaseFluidTransferNodeLogic<IWalkingComponent<FluidStack>>
{
    // Upgrades
    private boolean isCreative = false;
    private boolean isWorldInteraction = false;
    private boolean init = false;

    IFluidHandler connectedTank;
    public FluidWalker walker;

    public FluidTransferNodeLogic(IWalkingComponent<FluidStack> host) {
        super(host);
        walker = new FluidWalker(host);
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
        for (int i = 0; i < actionsThisTick; i ++)
        {
            if (isWorldInteraction)
            {
                importFluidsFromWorld();
            }
            else
            {
                if (connectedTank == null)
                {
                    updateSourceTank();
                }

                if (connectedTank != null)
                {
                    importFluids();
                }
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
                    int tryingToInsertAmount = fluid.amount / 2;
                    if (tryingToInsertAmount <= 0)
                    {
                        return;
                    }

                    FluidStack toInsert = fluid.copy();
                    toInsert.amount = tryingToInsertAmount;

                    int totalAccepted = 0;

                    for (TargetResolver.Target<IFluidHandler> target : targets)
                    {
                        if (toInsert.amount <= 0)
                        {
                            break;
                        }

                        int accepted = target.handler.fill(
                            ForgeDirection.getOrientation(target.side).getOpposite(),
                            toInsert,
                            false
                        );

                        accepted = Math.min(accepted, toInsert.amount);

                        toInsert.amount -= accepted;
                        totalAccepted += accepted;
                    }

                    if (totalAccepted <= 0)
                    {
                        return;
                    }

                    FluidStack remaining = fluid.copy();
                    remaining.amount = totalAccepted;

                    int actuallyInserted = 0;

                    for (TargetResolver.Target<IFluidHandler> target : targets)
                    {
                        if (remaining.amount <= 0)
                        {
                            break;
                        }

                        int filled = target.handler.fill(
                            ForgeDirection.getOrientation(target.side).getOpposite(),
                            remaining,
                            true
                        );

                        filled = Math.min(filled, remaining.amount);

                        remaining.amount -= filled;
                        actuallyInserted += filled;
                    }

                    if (actuallyInserted > 0 && !this.isCreative)
                    {
                        buffer.drain(actuallyInserted, true);
                    }
                }
            }
            walker.step(host.getWorld());
        }
    }

    public void importFluidsFromWorld()
    {
        ForgeDirection fromDir = host.getFacing();

        int spaceRemaining = buffer.getCapacity() - buffer.getFluidAmount();
        if (spaceRemaining <= 0)
        {
            return;
        }

        int x = host.getX() + fromDir.offsetX;
        int y = host.getY() + fromDir.offsetY;
        int z = host.getZ() + fromDir.offsetZ;

        World world = host.getWorld();
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (meta != 0)
        {
            return;
        }

        Fluid fluid;

        if (block == Blocks.water || block == Blocks.flowing_water)
        {
            if (!isInfiniteWaterSource(world, x, y, z))
            {
                return;
            }
            fluid = FluidRegistry.WATER;
        }
        else if (block == Blocks.lava || block == Blocks.flowing_lava)
        {
            return;
        }
        else
        {
            return;
        }

        FluidStack buffered = buffer.getFluid();
        if (buffered != null && !buffered.getFluid().equals(fluid))
        {
            return;
        }

        int fillAmount = Math.min(spaceRemaining, 1000);
        buffer.fill(new FluidStack(fluid, fillAmount), true);
    }

    private boolean isInfiniteWaterSource(World world, int x, int y, int z)
    {
        int sources = 0;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN)
            {
                continue;
            }

            int nx = x + dir.offsetX;
            int ny = y + dir.offsetY;
            int nz = z + dir.offsetZ;

            Block neighbor = world.getBlock(nx, ny, nz);
            int meta = world.getBlockMetadata(nx, ny, nz);

            if ((neighbor == Blocks.water || neighbor == Blocks.flowing_water) && meta == 0)
            {
                sources++;
                if (sources >= 2)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void importFluids()
    {
        ForgeDirection fromDir = host.getFacing().getOpposite();

        int spaceRemaining = buffer.getCapacity() - buffer.getFluidAmount();
        if (spaceRemaining <= 0)
        {
            return;
        }

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
    // Applicable upgrades: Creative, Speed, Stack, BFS, DFS, World interaction
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

    // Note that because of the power of the fluid slot players can now put fluids in manually too!
    // I see this as a win and doesn't break forward compat in any way.
    @Override
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

}
