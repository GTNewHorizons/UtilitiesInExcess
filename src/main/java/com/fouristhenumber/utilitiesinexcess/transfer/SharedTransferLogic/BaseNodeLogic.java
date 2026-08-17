package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.IUpgradeable;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.UpgradeInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BaseNodeLogic<T extends IWalkingComponent<V>, V> extends NetworkLogic<T> implements IUpgradeable {
    public static final int DEFAULT_STEPS_PER_SECOND = 2;
    protected int actionPerSecond = DEFAULT_STEPS_PER_SECOND;
    protected UpgradeInventory upgrades;

    private float progress = 0f;


    public BaseNodeLogic(T host) {
        super(host);
        this.upgrades = new UpgradeInventory(6, this);
    }

    public abstract void updateEntity();

    @Override
    public void markDirty() {
        host.markHostDirty();
    }

    @Override
    public void applySpeedUpgrade(ItemStack stack) {
        actionPerSecond += stack.stackSize;
    }

    // Naturally, due to how this works it can create aliasing in the location of the walker.
    // It could look strange to users, but I'm not sure the best way to fix this.
    public int actionsThisTick() {
        progress += actionPerSecond / 20f;

        int actions = (int) progress;
        progress -= actions;

        return actions; // number of actions this tick
    }

    @Override
    public void resetUpgrades() {
        this.actionPerSecond = DEFAULT_STEPS_PER_SECOND;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        this.upgrades.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        upgrades.readFromNBT(nbt);
    }

    @Override
    public void writeDesc(MCDataOutput output)
    {
        this.upgrades.writeDesc(output);
    }

    @Override
    public void readDesc(MCDataInput input)
    {
        this.upgrades.readDesc(input);
    }

    public abstract V getWalkingObject();
}
