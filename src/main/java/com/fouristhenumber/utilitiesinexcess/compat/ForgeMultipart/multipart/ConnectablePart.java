package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.BlockMultipart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.client.renderer.RenderGlobal.drawOutlinedBoundingBox;

public abstract class ConnectablePart extends MaterialBasedPart
{

    public static final Map<ForgeDirection, ForgeDirection[]> iteratorKey;
    static {
        Map<ForgeDirection, ForgeDirection[]> map = new EnumMap<>(ForgeDirection.class);

        map.put(ForgeDirection.DOWN,  new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.WEST, ForgeDirection.SOUTH, ForgeDirection.EAST});
        map.put(ForgeDirection.UP,    new ForgeDirection[]{ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST});
        map.put(ForgeDirection.EAST,  new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.UP});
        map.put(ForgeDirection.WEST,  new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.UP, ForgeDirection.SOUTH, ForgeDirection.DOWN});
        map.put(ForgeDirection.NORTH, new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.WEST, ForgeDirection.DOWN, ForgeDirection.EAST});
        map.put(ForgeDirection.SOUTH, new ForgeDirection[]{ForgeDirection.DOWN, ForgeDirection.WEST, ForgeDirection.UP, ForgeDirection.EAST});

        iteratorKey = map;
    }


    // What direction is "down" for the connectable part
    public ForgeDirection downDirection;


    ConnectablePart(int side)
    {
        this.downDirection = ForgeDirection.getOrientation(side);
    }

    // Is there a better way to implement this?
    protected boolean doPartsOccludeDirection(ForgeDirection side)
    {
        for(TMultiPart part: tile().jPartList())
        {
            if (part != this)
            {
                for (Cuboid6 cube : part.getSubParts())
                {
                    if (cube.intersects(getConnectionInDirection(side)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public abstract Cuboid6 getConnectionInDirection(ForgeDirection side);

    // TODO CACHE THESE RESULTS
    public boolean canConnectOnSide(ForgeDirection side) {
        if (tile() == null)
        {
            return false;
        }
        if (doPartsOccludeDirection(side))
        {
            return false;
        }
        int offsetX = x() + side.offsetX;
        int offsetY = y() + side.offsetY;
        int offsetZ = z() + side.offsetZ;
        Block block = world().getBlock(offsetX, offsetY, offsetZ);
        if (block.isSideSolid(world(), offsetX, offsetY, offsetZ, side.getOpposite()) &&
            block.renderAsNormalBlock() &&
            block.getMaterial().isOpaque())
        {
            return true;
        }
        else if (block instanceof BlockMultipart)
        {
            if (world().getTileEntity(offsetX, offsetY, offsetZ) instanceof TileMultipart mpTile)
            {
                for (TMultiPart part : mpTile.jPartList())
                {
                    if (part instanceof ConnectablePart cPart &&
                        part.getClass() == this.getClass() &&
                        cPart.downDirection == this.downDirection &&
                        !cPart.doPartsOccludeDirection(side.getOpposite()))
                    {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public int getConnectionMask()
    {
        int mask = 0b0000;
        ForgeDirection[] iteratorList = iteratorKey.get(downDirection);
        for (int i = 0; i < iteratorList.length; i++)
        {
            if (canConnectOnSide(iteratorList[i]))
            {
                mask = mask | 1 << i;
            }
        }
        return mask;
    }

    public int getCullMask(int relativeDirection)
    {
        return switch (relativeDirection) {
            case (1), (3) -> transformCullMask(0b110000);
            case (0), (2) -> transformCullMask(0b001100);
            default -> 0;
        };
    }

    private int transformCullMask(int mask)
    {
        switch(downDirection)
        {
            case DOWN, UP -> {
                return mask;
            }
            case NORTH, SOUTH ->
            {
                if (mask == 0b001100)
                {
                    return mask >> 2;
                }
                return mask;
            }
            case WEST, EAST ->
            {
                if (mask == 0b110000)
                {
                    return mask >> 4;
                }
                return mask;
            }
            case UNKNOWN ->
            {
                throw new IllegalArgumentException("Switch falloff in transforming the cullmask in a connectable part.");
            }
        }
        return mask;
    }

    @Override
    public void save(NBTTagCompound tag)
    {
        super.save(tag);
        tag.setInteger("side", this.downDirection.ordinal());
    }

    @Override
    public void load(NBTTagCompound tag)
    {
        super.load(tag);
        downDirection = ForgeDirection.getOrientation(tag.getInteger("side"));
    }

    @Override
    public void writeDesc(MCDataOutput packet)
    {
        super.writeDesc(packet);
        packet.writeInt(this.downDirection.ordinal());
    }

    protected int indexInFrame(ForgeDirection side) {
        ForgeDirection[] frame = iteratorKey.get(downDirection);
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == side) {
                return i;
            }
        }
        throw new IllegalArgumentException("Somehow multipart connectable is trying to access a direction outside of it's frame!");
    }

    public boolean drawConnecableHighLight(MovingObjectPosition hit, EntityPlayer player, float frame, Iterable<Pair<Integer, Cuboid6>> highlightCuboidList)
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) frame;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) frame;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) frame;

        ForgeDirection[] iteratorList = iteratorKey.get(downDirection);


        for (Pair<Integer, Cuboid6> cubeWithDirInfo : highlightCuboidList)
        {
            AxisAlignedBB bb = cubeWithDirInfo.second().toAABB().offset(x(), y(), z());
            if (cubeWithDirInfo.first() != -1)
            {
                switch(iteratorList[cubeWithDirInfo.first()])
                {
                    case UP:
                    {
                        bb.minY += 0.004F;
                        break;
                    }
                    case DOWN:
                    {
                        bb.maxY -= 0.004F;
                        break;
                    }
                    case NORTH:
                    {
                        bb.maxZ -= 0.004F;
                        break;
                    }
                    case SOUTH:
                    {
                        bb.minZ += 0.004F;
                        break;
                    }
                    case EAST:
                    {
                        bb.minX += 0.004F;
                        break;
                    }
                    case WEST:
                    {
                        bb.maxX -= 0.004F;
                        break;
                    }
                }
            }
            drawOutlinedBoundingBox(bb.expand(0.002F, 0.002F, 0.002F).getOffsetBoundingBox(-dx, -dy, -dz), -1);
        }
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        return true;
    }

}
