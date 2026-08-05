package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.IUpgradeable;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.UpgradeInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BaseNodeLogic<T extends ITransferNetworkComponent> extends NetworkLogic<T> implements IUpgradeable
{
    public static final int DEFAULT_STEPS_PER_SECOND = 2;
    protected int actionPerSecond = DEFAULT_STEPS_PER_SECOND;
    protected UpgradeInventory upgrades;

    private float progress = 0f;


    public BaseNodeLogic(T host) {
        super(host);
        this.upgrades = new UpgradeInventory(6, this);

    }

    @Override
    public void markDirty()
    {
        host.markHostDirty();
    }

    @Override
    public void applySpeedUpgrade(ItemStack stack)
    {
        actionPerSecond += stack.stackSize;
    }

    // Naturally, due to how this works it can create aliasing in the location of the walker.
    // It could look strange to users, but I'm not sure the best way to fix this.
    public int actionsThisTick()
    {
        progress += actionPerSecond / 20f;

        int actions = (int) progress;
        progress -= actions;

        return actions; // number of actions this tick
    }

    @Override
    public void resetUpgrades()
    {
        this.actionPerSecond = DEFAULT_STEPS_PER_SECOND;
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        this.upgrades.writeToNBT(nbt);

    }
    public void readFromNBT(NBTTagCompound nbt)
    {
        upgrades.readFromNBT(nbt);
    }
}
