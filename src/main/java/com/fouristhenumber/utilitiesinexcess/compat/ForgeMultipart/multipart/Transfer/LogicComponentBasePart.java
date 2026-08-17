package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util.PartGuiData;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public abstract class LogicComponentBasePart<T extends NetworkLogic> extends NetworkComponentBasePart implements IGuiHolder<PartGuiData>
{
    protected T logic;

    protected LogicComponentBasePart(int meta) {
        super(meta);
    }

    protected abstract T getLogic();

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        getLogic().writeToNBT(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        getLogic().readFromNBT(tag);
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        getLogic().writeDesc(packet);
    }

    @Override
    public ModularPanel buildUI(PartGuiData posGuiData, PanelSyncManager panelSyncManager, UISettings uiSettings) {
        return getLogic().buildUI(posGuiData, panelSyncManager, uiSettings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PartGuiData data, ModularPanel mainPanel) {
        return getLogic().createScreen(data, mainPanel);
    }
}
