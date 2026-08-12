package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.PipeType;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collections;

public class PipePart extends PartNetworkComponentBase
{
    public PipePart(int meta) {
        super(meta);
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
    public void render(Vector3 position, int pass) {

    }

    @Override
    public Cuboid6 getBounds() {
        return null;
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
    public int getConnectionMask(IBlockAccess world, int x, int y, int z) {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].getConnectionMask(world, x, y, z);
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].acceptsConnectionFrom(world, x, y, z, direction);
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].getInserter();
    }
}
