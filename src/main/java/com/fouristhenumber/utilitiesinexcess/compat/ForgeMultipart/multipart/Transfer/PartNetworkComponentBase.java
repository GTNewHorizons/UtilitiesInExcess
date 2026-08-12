package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.renderers.transfer.TransferNodeRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UEMultipart;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class PartNetworkComponentBase extends UEMultipart implements ITransferNetworkComponent, IConnectable
{
    public int meta;

    protected boolean doPartsOccludeDirection(ForgeDirection side) {
        for (TMultiPart part : tile().jPartList()) {
            if (part != this) {
                for (Cuboid6 cube : part.getSubParts()) {
                    if (cube.intersects(getConnectionInDirection(side))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Cuboid6 getConnectionInDirection(ForgeDirection side)
    {
        return new Cuboid6(PipeCollision.values()[side.ordinal() + 1].getCollisionBox());
    }

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
    public boolean renderStatic(Vector3 position, int pass)
    {
        render(position, pass);
        return true;
    }

    @Override
    public void render(Vector3 position, int pass)
    {
        TransferNodeRenderer.renderFlatNode(world(), (int) position.x, (int) position.y, (int) position.z, getBlock(), getBlock().getRenderType(), getRenderBlocks(world()), meta);
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return new DefaultInserter();
    }

}
