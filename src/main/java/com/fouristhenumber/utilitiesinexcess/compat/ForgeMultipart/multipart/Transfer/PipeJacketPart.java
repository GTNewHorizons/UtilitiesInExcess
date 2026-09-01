package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.microblock.MicroblockRender;
import codechicken.multipart.JPartialOcclusion;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TSlottedPart;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.IMaterialPart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Material;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEMultipart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEMultipartMaterialItem;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEPartFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PipeJacketPart extends UiEMultipart implements IMaterialPart, JPartialOcclusion, TSlottedPart
{
    public static final Cuboid6 bounds = new Cuboid6(.25, .25, .25, .75, .75, .75);
    public static final Cuboid6[] connectionBounds =
        {
            new Cuboid6(.25, 0, .25, .75, .25, .75), // DOWN
            new Cuboid6(.25, .75, .25, .75, 1, .75), // UP
            new Cuboid6(.25, .25, 0, .75, .75, .25), // NORTH
            new Cuboid6(.25, .25, .75, .75, .75, 1), // SOUTH
            new Cuboid6(0, .25, .25, .25, .75, .75), // WEST
            new Cuboid6(.75, .25, .25, 1, .75, .75), // EAST
        };

    private static final int[] CONNECTION_CULL_FACES =
        {
            1 << ForgeDirection.UP.ordinal(),     // DOWN
            1 << ForgeDirection.DOWN.ordinal(),   // UP
            1 << ForgeDirection.SOUTH.ordinal(),  // NORTH
            1 << ForgeDirection.NORTH.ordinal(),  // SOUTH
            1 << ForgeDirection.EAST.ordinal(),   // WEST
            1 << ForgeDirection.WEST.ordinal()    // EAST
        };

    public Material material;
    public static final String name = "pipe_jacket";
    public static final int COVER_SLOT = 0;

    public PipeJacketPart(int materialId)
    {
        this.material = new Material(materialId);
    }

    public PipeJacketPart(MCDataInput packet)
    {
        this.material = new Material(MicroMaterialRegistry.readMaterialID(packet));
    }

    @Override
    public Cuboid6 getBounds() {
        return bounds;
    }

    @Override
    public IIcon getBreakingIcon(Object subPart, int side) {
        return this.material.getBreakingIcon(subPart, side);
    }

    @Override
    public IIcon getBrokenIcon(int side) {
        return this.material.getBrokenIcon(side);
    }

    @Override
    public String getType() {
        return "pipe_jacket";
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Iterable<Cuboid6> getPartialOcclusionBoxes() {
        return Collections.singleton(bounds);
    }

    @Override
    public boolean allowCompleteOcclusion() {
        return false;
    }

    @Override
    public int getSlotMask() {
        return COVER_SLOT;
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts()
    {
        List<IndexedCuboid6> subParts = new ArrayList<>();
        subParts.add(new IndexedCuboid6(0, getBounds()));
        NetworkComponentBasePart part = getNetworkComponent();
        if (part != null)
        {
            int mask = part.getConnectionMask(world(), x(), y(), z());
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                if ((mask & (1 << dir.ordinal())) != 0)
                {
                    subParts.add(new IndexedCuboid6(0, connectionBounds[dir.ordinal()]));
                }
            }
        }
        return subParts;
    }

    // The best way to get the rendering for this thing is just check what else in is the block I think
    private NetworkComponentBasePart getNetworkComponent() {
        for (TMultiPart part : tile().jPartList())
        {
            if (part instanceof NetworkComponentBasePart)
            {
                return (NetworkComponentBasePart) part;
            }
        }
        return null;
    }

    @Override
    public boolean renderStatic(Vector3 position, int pass) {
        if (this.material.canMaterialRenderInPass(pass))
        {
            render(position, pass);
            return true;
        }
        return false;
    }

    @Override
    public void render(Vector3 position, int pass)
    {
        if (pass == -1)
        {
            MicroblockRender.renderCuboid(position, this.material.getIMaterial(), pass, bounds, 0);
            return;
        }

        List<IConnectable> connectables = IConnectable.getConnectables(world(), x(), y(), z());

        int mask = IConnectable.getConnectionMask(connectables, world(), x(), y(), z());

        // Cull the faces of the middle piece where a connection exists.
        MicroblockRender.renderCuboid(position, this.material.getIMaterial(), pass, bounds, mask);

        // Render each connected arm, culling the face touching the middle.
        for (int i = 0; i < connectionBounds.length; i++)
        {
            if ((mask & (1 << i)) == 0)
            {
                continue;
            }
            MicroblockRender.renderCuboid(position, this.material.getIMaterial(), pass, connectionBounds[i], CONNECTION_CULL_FACES[i]);
        }
    }

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        material.save(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        material.load(tag);
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        material.writeDesc(packet);
    }

    public ItemStack pickItem(MovingObjectPosition hit) {
        return UiEMultipartMaterialItem.createStack(this);
    }

    public void onPartChanged(TMultiPart unused)
    {
        boolean shouldDrop = true;
        for (TMultiPart part : tile().jPartList())
        {
            if (part instanceof NetworkComponentBasePart)
            {
                shouldDrop = false;
            }
        }
        if (shouldDrop)
        {
            tile().dropItems(this.getDrops());
            tile().remPart(this);
        }
    }
}

