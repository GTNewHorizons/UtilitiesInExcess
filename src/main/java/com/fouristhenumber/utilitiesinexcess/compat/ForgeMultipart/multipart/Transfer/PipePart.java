package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockPipe;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.PipeType;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util.PartGuiHandler;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FilterPipeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PipePart extends LogicComponentBasePart<FilterPipeLogic>
{
    public PipePart(int meta) {
        super(meta);
    }

    private FilterPipeLogic logic;

    protected FilterPipeLogic getLogic() {
        if (logic == null)
        {
            logic = new FilterPipeLogic(this);
        }
        return logic;
    }

    public PipePart(MCDataInput packet)
    {
        super(packet.readInt());
        getLogic().readDesc(packet);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.TRANSFER_PIPE.get();
    }

    @Override
    public String getType() {
        return ConversionRegistry.Pipe.getName();
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes()
    {
        List<Cuboid6> cuboid6s = new ArrayList<>();
        for (AxisAlignedBB aabb : BlockPipe.getBlockCenteredCollisionCandidates(world(), x(), y(), z(), meta))
        {
            cuboid6s.add(new Cuboid6(aabb));
        }
        return cuboid6s;
    }

    private Cuboid6 getBlockBounds(IBlockAccess world, int x, int y, int z)
    {
        List<IConnectable> connectables = IConnectable.getConnectables(world, x, y, z);

        if (connectables.isEmpty())
        {
            return new Cuboid6(
                new Vector3(0.375, 0.375, 0.375),
                new Vector3(0.625, 0.625, 0.625)
            );
        }

        int mask = IConnectable.getConnectionMask(connectables, world, x, y, z);

        double minY = (mask & (1 << 0)) != 0 ? 0.0 : 0.375;
        double maxY = (mask & (1 << 1)) != 0 ? 1.0 : 0.625;

        double minZ = (mask & (1 << 2)) != 0 ? 0.0 : 0.375;
        double maxZ = (mask & (1 << 3)) != 0 ? 1.0 : 0.625;

        double minX = (mask & (1 << 4)) != 0 ? 0.0 : 0.375;
        double maxX = (mask & (1 << 5)) != 0 ? 1.0 : 0.625;

        return new Cuboid6(
            new Vector3(minX, minY, minZ),
            new Vector3(maxX, maxY, maxZ)
        );
    }

    @Override
    public Cuboid6 getBounds() {
        return getBlockBounds(world(), x(), y(), z());
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(new Cuboid6(PipeCollision.MIDDLE.getCollisionBox()));
    }

    @Override
    public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = PipeType.values()[meta].validWalkDirections(world, x, y, z, fromDirection, walkingComponent);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            int bit = 1 << dir.ordinal();
            if ((mask & bit) == 0)
            {
                continue;
            }
            if (doPartsOccludeDirection(dir))
            {
                mask &= ~bit;
            }
        }
        return mask;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z)
    {
        int mask = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (getConnection(world, x, y, z, dir))
            {
                mask |= 1 << dir.ordinal();
            }
        }
        return mask;
    }

    @Override
    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        return PipeType.values()[meta].getConnection(world, x, y, z, dir) && !doPartsOccludeDirection(dir);
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        if (!PipeType.values()[meta].acceptsConnectionFrom(world, x, y, z, direction) )
        {
            return false;
        }
        return !doPartsOccludeDirection(direction);
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return PipeType.values()[meta].getInserter();
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {
        List<IConnectable> connectables = IConnectable.getConnectables(
            world(),
            x(),
            y(),
            z()
        );
        int mask = IConnectable.getConnectionMask(
            connectables,
            world(),
            x(),
            y(),
            z()
        );
        List<IndexedCuboid6> parts = new ArrayList<>(7);
        parts.add(new IndexedCuboid6(
            PipeCollision.MIDDLE.ordinal(),
            new Cuboid6(PipeCollision.MIDDLE.getCollisionBox())
        ));

        for (PipeCollision collision : PipeCollision.values())
        {
            if (collision == PipeCollision.MIDDLE)
                continue;

            int bit = collision.ordinal() - 1;
            if ((mask & (1 << bit)) != 0)
            {
                parts.add(new IndexedCuboid6(
                    collision.ordinal(),
                    new Cuboid6(collision.getCollisionBox())
                ));
            }
        }
        return parts;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean drawHighlight(
        MovingObjectPosition hit,
        EntityPlayer player,
        float frame)
    {
        Cuboid6 bounds = getBlockBounds(world(), x(), y(), z());

        double d0 = player.lastTickPosX
            + (player.posX - player.lastTickPosX) * frame;

        double d1 = player.lastTickPosY
            + (player.posY - player.lastTickPosY) * frame;

        double d2 = player.lastTickPosZ
            + (player.posZ - player.lastTickPosZ) * frame;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            bounds.min.x + x() - d0,
            bounds.min.y + y() - d1,
            bounds.min.z + z() - d2,
            bounds.max.x + x() - d0,
            bounds.max.y + y() - d1,
            bounds.max.z + z() - d2
        );

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(
            GL11.GL_SRC_ALPHA,
            GL11.GL_ONE_MINUS_SRC_ALPHA
        );
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        RenderGlobal.drawOutlinedBoundingBox(box, -1);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        return true;
    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack stack)
    {
        if (!world().isRemote && this.meta == PipeType.FILTER.getMeta())
        {
            Seq<TMultiPart> parts = tile().partList();

            int index = -1;
            for (int i = 0; i < parts.size(); i++)
            {
                if (parts.apply(i) == this)
                {
                    index = i;
                    break;
                }
            }
            PartGuiHandler.open(player, this, index);
            return true;
        }
        return false;
    }
}
