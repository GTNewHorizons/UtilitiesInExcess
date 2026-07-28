package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.EnergyWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnergyTransferNodeLogic extends NetworkLogic<TileEntityEnergyTransferNode> implements IInventory {
    ItemStack[] upgrades = new ItemStack[getSizeInventory()];

    public EnergyWalker walker;
    public List<IEnergyProvider> sources = new ArrayList<IEnergyProvider>();
    public List<IEnergyReceiver> sinks = new ArrayList<IEnergyReceiver>();

    public EnergyTransferNodeLogic(TileEntityEnergyTransferNode host) {
        super(host);
    }

    // Energy nodes seem to have a few rules.
    // 1. If the walker finds a IEnergyReceiver on a normal pipe it's treated as a receiver even if it's an
    // IEnergyProvider. x
    // 2. If the walker finds a IEnergyProvider adjacent to the node, it's treated as a provider even if it's also
    // an IEnergyReceiver. x
    // 3. If the walker finds a IEnergyProvider adjacent to an energy extraction pipe it's treated as a
    // provider even if it's an IEnergyReceiver. x
    // 4. If the walker finds a IEnergyReceiver that is not an IEnergyProvider adjacent to a node it's treated
    // as a receiver. x
    // 5. Walkers of any type may walk through energy nodes in any valid direction.
    // 6. If the walker finds a IEnergyReceiver that is not an IEnergyProvider adjacent to an energy extraction pipe
    // it does not supply it power. x
    public void updateEntity()
    {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        if (!sources.isEmpty())
        {
            importEnergy();
        }

        if (!sinks.isEmpty())
        {
            exportEnergy();
        }

        List<TargetResolver.Target<IEnergyConnection>> targets = walker.getValidTargets(host.getWorld());
        if (!targets.isEmpty())
        {
            for (TargetResolver.Target<IEnergyConnection> target : targets)
            {
                if (walker.isOnExtractionPipe(host.getWorld()))
                {
                    if (target.handler instanceof IEnergyProvider source)
                    {
                        sources.add(source);
                    }
                }
                else if (walker.isAtOrigin()) // Means we're on the node
                {
                    if (target.handler instanceof IEnergyProvider source)
                    {
                        sources.add(source);
                    }
                    else if (target.handler instanceof IEnergyReceiver sink)
                    {
                        sinks.add(sink);
                    }
                }
                else if (target.handler instanceof IEnergyReceiver sink)
                {
                    sinks.add(sink);
                }
            }
        }
        walker.step(host.getWorld());
    }


    public void importEnergy()
    {

    }

    public void exportEnergy()
    {

    }

    @Override
    public int getSizeInventory() {
        return 6;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return upgrades[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }
}
