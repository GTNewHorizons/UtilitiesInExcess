package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.IUpgradeable;
import net.minecraft.item.ItemStack;

public abstract class BaseNodeLogic<T extends ITransferNetworkComponent> extends NetworkLogic<T> implements IUpgradeable
{
    public static final int DEFAULT_STEPS_PER_SECOND = 2;
    protected int actionPerSecond = DEFAULT_STEPS_PER_SECOND;

    private float progress = 0f;


    public BaseNodeLogic(T host) {
        super(host);
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
}
