package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Vector3;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UEMultipart;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class PartNetworkComponentBase<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends UEMultipart implements ITransferNetworkComponent
{
    protected T logic;
    public int meta;

    protected PartNetworkComponentBase(int meta) {
        this.meta = meta;
    }

    @Override
    public World getWorld() {
        return world();
    }

    @Override
    public int getX() {
        return x();
    }

    @Override
    public int getY() {
        return y();
    }

    @Override
    public int getZ() {
        return z();
    }

    @Override
    public void markHostDirty() {
        tile().markDirty();
    }

    @Override
    public int getMeta()
    {
        return meta;
    }

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        tag.setInteger("meta", meta);
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        meta = tag.getInteger("meta");
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        packet.writeInt(meta);
    }

    public abstract Block getBlock();

    protected abstract T getLogic();

    @Override
    public IIcon getBreakingIcon(Object subPart, int side) {
        return getBlock().getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBrokenIcon(int side) {
        return getBlock().getIcon(side, meta);
    }

    @Override
    public void render(Vector3 position, int pass)
    {
        RenderingRegistry.instance().renderWorldBlock(RenderBlocks.getInstance(), getWorld(), (int) position.x, (int) position.y, (int) position.z, getBlock(), getBlock().getRenderType());
    }

}
