package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.fouristhenumber.utilitiesinexcess.transfer.gui.NodeGui;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.WalkerBase;
import goodgenerator.blocks.myFluids.BaseFluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class BaseFluidTransferNodeLogic extends BaseNodeLogic
{
    public static final int maxFluidAmount = 8000;
    public static final int DEFAULT_MAX_DRAIN_AMOUNT = 200;
    public int maxDrainAmount = DEFAULT_MAX_DRAIN_AMOUNT;
    protected FluidTank buffer = new FluidTank(maxFluidAmount);

    protected FluidWalker walker;

    public BaseFluidTransferNodeLogic(IWalkingComponent host)
    {
        super(host);
    }

    @Override
    public FluidStack getWalkingObject() {
        return buffer.getFluid();
    }

    @Override
    public void invalidateInventory()
    {
        super.invalidateInventory();
        buffer.setFluid(null);
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
        if (logic instanceof BaseFluidTransferNodeLogic fluidNodeLogic)
        {
            this.buffer = fluidNodeLogic.buffer;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagCompound fluidTag = new NBTTagCompound();
        buffer.writeToNBT(fluidTag);
        nbt.setTag("Fluid", fluidTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        buffer.readFromNBT(nbt.getCompoundTag("Fluid"));
    }

    @Override
    public void writeDesc(MCDataOutput output)
    {
        super.writeDesc(output);
        output.writeFluidStack(buffer.getFluid());
    }

    @Override
    public void readDesc(MCDataInput input)
    {
        super.readDesc(input);
        buffer.setFluid(input.readFluidStack());
    }

    // ======================================= UI =======================================
    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> "Search Location: " + walker.getLocationString());
        return NodeGui.buildUI(upgrades, "transfer_node_upgrades", getInventoryName(), searchLocationSyncer,
            () -> new FluidSlot().syncHandler(buffer), syncManager
        );
    }

}
