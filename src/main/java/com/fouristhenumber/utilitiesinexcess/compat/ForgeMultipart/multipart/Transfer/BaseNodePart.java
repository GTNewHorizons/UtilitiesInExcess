package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util.PartGuiHandler;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.BaseNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.NodeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase.getFacingOrdinal;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic.isValidConnectable;

public abstract class BaseNodePart <T extends BaseNodeLogic<?, V>, V> extends PartNetworkComponentBase
    implements IWalkingComponent<V>, IGuiHolder<PosGuiData>
{
    protected T logic;
    private boolean initialized = false;

    public BaseNodePart(int meta) {
        super(meta);
    }

    @Override
    public void update()
    {
        if (world().isRemote)
        {
            return;
        }

        if (!initialized) {
            init();
        }

        getLogic().updateEntity();
    }


    private void init() {
        if (initialized) {
            return;
        }

        initialized = true;
    }

    protected abstract T getLogic();

    @Override
    public Cuboid6 getBounds() {
        return new Cuboid6(BlockNodeBase.getBoundsAABB(meta, this.getConnectionMask(world(), x(), y(), z())));
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Stream.concat(
                Arrays.stream(NodeCollision.values()[getFacing().ordinal()].getOcclusionBoxes()),
                Stream.of(PipeCollision.MIDDLE.getCollisionBox())
            )
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
        return !doPartsOccludeDirection(direction);
    }


    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
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
        return mask;
    }

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        getLogic().writeToNBT(tag);
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        getLogic().readFromNBT(tag);
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        getLogic().writeDesc(packet);
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
        ForgeDirection facing = this.getFacing();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (dir != facing)
            {
                if (!doPartsOccludeDirection(dir) && isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir))
                {
                    mask |= 1 << dir.ordinal();
                }
            }
        }
        return mask;
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        return Collections.singleton(new IndexedCuboid6(0, new Cuboid6(BlockNodeBase.getBoundsAABB(meta, this.getConnectionMask(world(), x(), y(), z())))));
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

    @Override
    public ModularPanel buildUI(PosGuiData posGuiData, PanelSyncManager panelSyncManager, UISettings uiSettings) {
        return getLogic().buildUI(posGuiData, panelSyncManager, uiSettings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return getLogic().createScreen(data, mainPanel);
    }
}
