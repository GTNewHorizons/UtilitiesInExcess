package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.ISBRHPart;
import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEMultipart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.MetaOverrideWorld;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.FMLRenderAccessLibrary;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class NetworkComponentBasePart extends UiEMultipart implements ITransferNetworkComponent, IConnectable, ISBRHPart
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

    protected NetworkComponentBasePart(int meta) {
        this.meta = meta;
    }

    public abstract Block getBlock();

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
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        FMLRenderAccessLibrary.renderWorldBlock(renderer, new MetaOverrideWorld(world, x, y, z, meta), x, y, z, getBlock(), ModelISBRH.JSON_ISBRH_ID);
        return true;
    }
    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return new DefaultInserter();
    }
}
