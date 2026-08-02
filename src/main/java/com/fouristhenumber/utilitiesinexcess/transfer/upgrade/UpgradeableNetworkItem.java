package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import com.cleanroommc.modularui.widgets.slot.IOnSlotChanged;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.item.ItemStack;

public abstract class UpgradeableNetworkItem<T extends ITransferNetworkComponent> extends NetworkLogic<T> implements IOnSlotChanged
{

    public UpgradeableNetworkItem(T host) {
        super(host);
    }

    @Override
    public void onChange(ItemStack newItem, boolean onlyAmountChanged, boolean client, boolean init)
    {

    }

    public void applySpeedUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyStackUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyWorldInteractionUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyCreativeUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyFilterUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyAdvFilterUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyBFSUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyDFSUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyPseudoRoundRobin(NetworkLogic<?> node)
    {

    }

    public void applyTransmitterUpgrade(NetworkLogic<?> node)
    {

    }

    public void applyReceiverUpgrade(NetworkLogic<?> node)
    {

    }


}

