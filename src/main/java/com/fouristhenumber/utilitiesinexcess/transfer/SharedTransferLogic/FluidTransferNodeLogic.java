package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;

public class FluidTransferNodeLogic extends BaseFluidTransferNodeLogic {

    // Upgrades
    private boolean isCreative = false;
    private boolean isWorldInteraction = false;

    IFluidHandler connectedTank;

    public FluidTransferNodeLogic(IWalkingComponent host) {
        super(host);
        walker = new FluidWalker(host);
    }

    public void updateEntity() {
        if (host.getWorld().isRemote) {
            return;
        }

        if (!init) {
            walker.init();
            upgrades.init();
            init = true;
        }

        int actionsThisTick = actionsThisTick();
        for (int i = 0; i < actionsThisTick; i++) {
            if (isWorldInteraction) {
                importFluidsFromWorld();
            } else {
                updateSourceTank();
                if (connectedTank != null) {
                    importFluids();
                }
            }

            FluidStack fluid = buffer.getFluid();
            if (fluid == null) {
                walker.reset();
                return;
            }

            List<TargetResolver.Target<IFluidHandler>> targets = walker.getValidTargets(host.getWorld());

            if (!targets.isEmpty()) {
                int tryingToInsertAmount = (fluid.amount + 1) / 2;

                FluidStack toInsert = fluid.copy();
                toInsert.amount = tryingToInsertAmount;

                int totalAccepted = 0;

                for (TargetResolver.Target<IFluidHandler> target : targets) {
                    if (toInsert.amount <= 0) {
                        break;
                    }

                    int accepted = target.handler.fill(
                        ForgeDirection.getOrientation(target.side)
                            .getOpposite(),
                        toInsert,
                        false);

                    accepted = Math.max(0, Math.min(accepted, toInsert.amount));

                    toInsert.amount -= accepted;
                    totalAccepted += accepted;
                }

                if (totalAccepted > 0) {
                    FluidStack remaining = fluid.copy();
                    remaining.amount = totalAccepted;

                    int actuallyInserted = 0;

                    for (TargetResolver.Target<IFluidHandler> target : targets) {
                        if (remaining.amount <= 0) {
                            break;
                        }

                        int filled = target.handler.fill(
                            ForgeDirection.getOrientation(target.side)
                                .getOpposite(),
                            remaining,
                            true);

                        filled = Math.max(0, Math.min(filled, remaining.amount));

                        remaining.amount -= filled;
                        actuallyInserted += filled;
                    }

                    if (actuallyInserted > 0 && !this.isCreative) {
                        buffer.drain(actuallyInserted, true);
                        host.markHostDirty();
                    }
                }
            }

            walker.step(host.getWorld());
        }
    }

    public void importFluidsFromWorld() {
        ForgeDirection fromDir = ((IWalkingComponent) host).getFacing();

        int spaceRemaining = buffer.getCapacity() - buffer.getFluidAmount();
        if (spaceRemaining <= 0) {
            return;
        }

        int x = host.getX() + fromDir.offsetX;
        int y = host.getY() + fromDir.offsetY;
        int z = host.getZ() + fromDir.offsetZ;

        World world = host.getWorld();
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (meta != 0) {
            return;
        }

        Fluid fluid;

        if (block == Blocks.water || block == Blocks.flowing_water) {
            if (!isInfiniteWaterSource(world, x, y, z)) {
                return;
            }
            fluid = FluidRegistry.WATER;
        } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
            return;
        } else {
            return;
        }

        FluidStack buffered = buffer.getFluid();
        if (buffered != null && !buffered.getFluid()
            .equals(fluid)) {
            return;
        }

        int fillAmount = Math.min(spaceRemaining, 1000);
        buffer.fill(new FluidStack(fluid, fillAmount), true);
        host.markHostDirty();
    }

    private boolean isInfiniteWaterSource(World world, int x, int y, int z) {
        int sources = 0;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
                continue;
            }

            int nx = x + dir.offsetX;
            int ny = y + dir.offsetY;
            int nz = z + dir.offsetZ;

            Block neighbor = world.getBlock(nx, ny, nz);
            int meta = world.getBlockMetadata(nx, ny, nz);

            if ((neighbor == Blocks.water || neighbor == Blocks.flowing_water) && meta == 0) {
                sources++;
                if (sources >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    public void importFluids() {
        ForgeDirection fromDir = ((IWalkingComponent) host).getFacing()
            .getOpposite();

        int spaceRemaining = buffer.getCapacity() - buffer.getFluidAmount();
        if (spaceRemaining <= 0) {
            return;
        }

        int drainAmount = Math.min(spaceRemaining, maxDrainAmount);

        FluidStack bufferedFluid = buffer.getFluid();

        if (bufferedFluid == null) {
            FluidStack drainableFluid = connectedTank.drain(fromDir, drainAmount, false);
            if (drainableFluid != null && drainableFluid.amount > 0) {
                FluidStack drained = connectedTank.drain(fromDir, drainableFluid.amount, true);
                if (drained != null) {
                    buffer.fill(drained, true);
                    host.markHostDirty();
                }
            }
        } else {
            if (connectedTank.canDrain(fromDir, bufferedFluid.getFluid())) {
                FluidStack request = new FluidStack(bufferedFluid.getFluid(), drainAmount);
                FluidStack drainableFluid = connectedTank.drain(fromDir, request, false);
                if (drainableFluid != null && drainableFluid.amount > 0) {
                    FluidStack drained = connectedTank
                        .drain(fromDir, new FluidStack(bufferedFluid.getFluid(), drainableFluid.amount), true);
                    if (drained != null) {
                        buffer.fill(drained, true);
                        host.markHostDirty();
                    }
                }
            }
        }
    }

    public void updateSourceTank() {
        ForgeDirection facing = ((IWalkingComponent) host).getFacing();
        TileEntity neighbor = host.getWorld()
            .getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IFluidHandler tank) {
            connectedTank = tank;
        }
    }

    // ======================================= Upgrades =======================================
    // Applicable upgrades: Creative, Speed, Stack, BFS, DFS, World interaction
    @Override
    public void resetUpgrades() {
        super.resetUpgrades();
        this.walker.setStepper(new RandomStepper());
        this.isCreative = false;
        this.maxDrainAmount = DEFAULT_MAX_DRAIN_AMOUNT;
        this.isWorldInteraction = false;
    }

    @Override
    public void applySearchDepthUpgrade(ItemStack stack) {
        this.walker.setStepper(new DFSStepper());
    }

    @Override
    public void applySearchBreadthUpgrade(ItemStack stack) {
        this.walker.setStepper(new BFSStepper());
    }

    @Override
    public void applyCreativeUpgrade(ItemStack stack) {
        this.isCreative = true;
    }

    @Override
    public void applyStackUpgrade(ItemStack stack) {
        this.maxDrainAmount = maxFluidAmount;
    }

    @Override
    public void applyWorldInteractionUpgrade(ItemStack stack) {
        this.isWorldInteraction = true;
    }

    public String getInventoryName() {
        return "uie.gui.title.fluid_transfer_node.name";
    }
}
