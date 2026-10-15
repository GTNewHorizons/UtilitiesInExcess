package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.IUpgradeable;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.UpgradeInventory;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

public abstract class BaseNodeLogic extends NetworkLogic implements IUpgradeable, ITickableLogic, IWalkingLogic {

    public static final int DEFAULT_STEPS_PER_SECOND = 2;
    protected int actionPerSecond = DEFAULT_STEPS_PER_SECOND;
    protected UpgradeInventory upgrades;
    protected boolean init = false;

    private float progress = 0f;

    public BaseNodeLogic(IWalkingComponent host) {
        super(host);
        this.upgrades = new UpgradeInventory(6, this);
    }

    @Override
    public void invalidateInventory() {
        upgrades.clearInv();
    }

    @Override
    public void initFromOtherLogic(NetworkLogic logic) {
        if (logic instanceof BaseNodeLogic nodeLogic) {
            this.getWalker()
                .initFromOtherWalker(nodeLogic.getWalker());
            this.upgrades = nodeLogic.upgrades;
        }
        this.init = true;
    }

    public void dropContents(World world, int x, int y, int z) {
        this.upgrades.dropContents(world, x, y, z);
    }

    @Override
    public void markDirty() {
        host.markHostDirty();
    }

    @Override
    public void applySpeedUpgrade(ItemStack stack) {
        actionPerSecond += stack.stackSize;
    }

    public abstract String getInventoryName();

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
    public void writeToNBT(NBTTagCompound nbt) {
        this.upgrades.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        upgrades.readFromNBT(nbt);
    }

    @Override
    public void writeDesc(MCDataOutput output) {
        this.upgrades.writeDesc(output);
    }

    @Override
    public void readDesc(MCDataInput input) {
        this.upgrades.readDesc(input);
    }
}
