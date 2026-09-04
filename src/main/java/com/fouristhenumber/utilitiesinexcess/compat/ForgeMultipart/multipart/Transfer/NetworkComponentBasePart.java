package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.ISBRHPart;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.JPartialOcclusion;
import codechicken.multipart.JsonModeledPart;
import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEMultipart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEMultipartMaterialItem;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.BlockMetaOverrideWorld;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.MetaOverrideWorld;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.FMLRenderAccessLibrary;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collections;

public abstract class NetworkComponentBasePart extends UiEMultipart implements ITransferNetworkComponent, IConnectable, JsonModeledPart, JNormalOcclusion
{
    public int meta;

    protected boolean doPartsOccludeDirection(ForgeDirection side) {
        Cuboid6 connectionBox = getConnectionInDirection(side);

        for (TMultiPart part : tile().jPartList()) {
            if (part == this || part instanceof PipeJacketPart) { // Don't count pipe jacket in occlusion for network parts.
                continue;
            }

            if (part instanceof JNormalOcclusion) {
                for (Cuboid6 cube : ((JNormalOcclusion) part).getOcclusionBoxes()) {
                    if (cube.intersects(connectionBox)) {
                        return true;
                    }
                }
            }

            if (part instanceof JPartialOcclusion) {
                for (Cuboid6 cube : ((JPartialOcclusion) part).getPartialOcclusionBoxes()) {
                    if (cube.intersects(connectionBox)) {
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

    @Override
    public World getWorld() {
        return world();
    }

    @Override
    public IBlockAccess getRenderWorld()
    {
        return new MetaOverrideWorld(world(), x(), y(), z(), meta);
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
        return ModelISBRH.INSTANCE.get().getParticleIcon(new BlockMetaOverrideWorld(world(), x(), y(), z(), meta, this.getBlock()), x(), y(), z());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBrokenIcon(int side) {
        return ModelISBRH.INSTANCE.get().getParticleIcon(new BlockMetaOverrideWorld(world(), x(), y(), z(), meta, this.getBlock()), x(), y(), z());
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return new DefaultInserter();
    }

    // Pipes are never allowed in the same space as other network component bases.
    @Override
    public boolean occlusionTest(TMultiPart part)
    {
        if (this instanceof PipePart && part instanceof NetworkComponentBasePart) {
            return false;
        }

        if (this instanceof NetworkComponentBasePart && part instanceof PipePart) {
            return false;
        }

        return super.occlusionTest(part);
    }

    public ItemStack pickItem(MovingObjectPosition hit)
    {
        return new ItemStack(Item.getItemFromBlock(getBlock()), 1, meta);
    }

    @Override
    public Iterable<ItemStack> getDrops()
    {
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock(getBlock()), 1, meta));
    }

    public float getStrength(MovingObjectPosition hit , EntityPlayer player)
    {
        return 30 * getBlock().getBlockHardness(world(), hit.blockX, hit.blockY, hit.blockZ); // It's a weird calc but whatever.
    }
}
