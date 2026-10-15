package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNodeBase;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util.PartGuiData;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import static com.fouristhenumber.utilitiesinexcess.utils.InventoryUtils.dropStack;

public abstract class LogicComponentBasePart extends NetworkComponentBasePart implements IGuiHolder<PartGuiData>
{
    protected NetworkLogic logic;

    protected LogicComponentBasePart(int meta) {
        super(meta);
    }

    protected abstract NetworkLogic getLogic();

    public void initFromTE(TileEntityNetworkComponentBase base)
    {
        getLogic().initFromOtherLogic(base.getLogic());
    }

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

    // Override this because why do extra allocations to drop things?
    @Override
    public void harvest(MovingObjectPosition hit, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode)
        {
            logic.dropContents(world(), x(), y(), z());
            this.dropSelf(world(), x(), y(), z());
        }
        this.tile().remPart(this);
    }

    private void dropSelf(World world, int x, int y, int z)
    {
        dropStack(world, x, y, z, new ItemStack(Item.getItemFromBlock(getBlock()), 1, BlockNodeBase.getType(meta)));
    }

    @Override
    public void invalidateConvertedTile()
    {
        TileEntity teToInvalidate = world().getTileEntity(x(), y(), z());
        if (teToInvalidate instanceof TileEntityTransferNodeBase baseNodeTe)
        {
            baseNodeTe.invalidateInventory();
        }
    }
}
