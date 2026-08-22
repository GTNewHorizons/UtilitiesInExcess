package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util.PartGuiHandler;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.BaseNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.NodeCollision;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase.getFacingOrdinal;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic.isValidConnectable;

public abstract class BaseNodePart <T extends BaseNodeLogic<?, V>, V> extends LogicComponentBasePart<T>
    implements IWalkingComponent<V>
{
    public BaseNodePart(int meta) {
        super(meta);
    }

    @Override
    public void update()
    {
        getLogic().updateEntity();
    }

    @Override
    public Cuboid6 getBounds() {
        return new Cuboid6(NodeCollision.values()[getFacingOrdinal(meta)].getBoundingBox().copy());
    }


    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Arrays.stream(NodeCollision.values()[getFacing().ordinal()].getOcclusionBoxes())
            .map(AxisAlignedBB::copy)
            .map(Cuboid6::new)
            .toList();
    }

    @Override
    public abstract String getType();

    @Override
    public V getWalkingObject() {
        return getLogic().getWalkingObject();
    }

    @Override
    public ForgeDirection getFacing() {
        return BlockNodeBase.getFacing(meta);
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        if (tile() == null) {
            return false;
        }
        return direction != getFacing() && !doPartsOccludeDirection(direction);
    }


    @Override
    public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        int facing = getFacingOrdinal(meta);
        if (facing < 6)
        {
            mask &= ~(1 << facing);
        }
        if (fromDirection != ForgeDirection.UNKNOWN)
        {
            mask &= ~(1 << fromDirection.ordinal());
        }
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
    public Iterable<Cuboid6> getCollisionBoxes()
    {
        List<Cuboid6> cuboid6s = new ArrayList<>();
        for (AxisAlignedBB aabb : BlockNodeBase.getBlockCenteredCollisionCandidates(world(), x(), y(), z(), meta))
        {
            cuboid6s.add(new Cuboid6(aabb));
        }
        return cuboid6s;
    }

    // Needs to be separate from the block implementation because we care about occluding parts in the current block
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

    // Needs to be separate from the block implementation because we care about occluding parts in the current block
    @Override
    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        ForgeDirection facing = this.getFacing();
        if (dir != facing)
        {
            return !doPartsOccludeDirection(dir) && isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
        }
        return false;
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        return Collections.singleton(new IndexedCuboid6(0, this.getBounds()));
    }

    @Override
    public boolean activate(EntityPlayer player, MovingObjectPosition hit, ItemStack stack)
    {
        if (!world().isRemote)
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
