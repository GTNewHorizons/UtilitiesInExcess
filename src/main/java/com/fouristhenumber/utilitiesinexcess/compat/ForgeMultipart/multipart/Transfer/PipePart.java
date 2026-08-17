package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.ISBRHPart;
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
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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

        if (!connectables.isEmpty())
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
                mask &= ~(1 << dir.ordinal());
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
    public Iterable<IndexedCuboid6> getSubParts() {
        return Collections.singleton(new IndexedCuboid6(0, new Cuboid6(this.getBlockBounds(world(), x(), y(), z()))));
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
        }
        return true;
    }
}
