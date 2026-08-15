package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.ISBRHPart;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockPipe;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.PipeType;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.renderers.transfer.TransferPipeRenderer.RenderPipes;

public class PipePart extends PartNetworkComponentBase implements ISBRHPart
{
    public PipePart(int meta) {
        super(meta);
    }

    public PipePart(MCDataInput packet)
    {
        super(packet.readInt());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        RenderPipes(this.getConnectionMask(world, x, y, z), x, y, z, this.getBlock(), renderer);
        return true;
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
        IConnectable connectable = IConnectable.getConnectable(world, x, y, z);

        if (connectable == null)
        {
            return new Cuboid6(
                new Vector3(0.375, 0.375, 0.375),
                new Vector3(0.625, 0.625, 0.625)
            );
        }

        int mask = connectable.getConnectionMask(world, x, y, z);

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
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent) {
        return PipeType.values()[meta].validWalkDirections(world, x, y, z, fromDirection, walkingComponent);
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z)
    {
        int mask = PipeType.values()[world.getBlockMetadata(x, y, z)].getConnectionMask(world, x, y, z);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (doPartsOccludeDirection(dir))
            {
                mask &= ~(1 << dir.ordinal());
            }
        }
        return mask;
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        if (doPartsOccludeDirection(direction))
        {
            return false;
        }
        return PipeType.values()[world.getBlockMetadata(x, y, z)].acceptsConnectionFrom(world, x, y, z, direction);
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].getInserter();
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        return Collections.singleton(new IndexedCuboid6(0, new Cuboid6(this.getBlockBounds(world(), x(), y(), z()))));
    }
}
