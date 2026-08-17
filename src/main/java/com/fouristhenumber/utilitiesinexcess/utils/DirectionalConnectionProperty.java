package com.fouristhenumber.utilitiesinexcess.utils;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Type;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable.getConnectables;

public class DirectionalConnectionProperty implements BlockProperty<Boolean>
{
    public static final BlockProperty<Boolean> NORTH =
        new DirectionalConnectionProperty("connection_north", ForgeDirection.NORTH);

    public static final BlockProperty<Boolean> SOUTH =
        new DirectionalConnectionProperty("connection_south", ForgeDirection.SOUTH);

    public static final BlockProperty<Boolean> EAST =
        new DirectionalConnectionProperty("connection_east", ForgeDirection.EAST);

    public static final BlockProperty<Boolean> WEST =
        new DirectionalConnectionProperty("connection_west", ForgeDirection.WEST);

    public static final BlockProperty<Boolean> UP =
        new DirectionalConnectionProperty("connection_up", ForgeDirection.UP);

    public static final BlockProperty<Boolean> DOWN =
        new DirectionalConnectionProperty("connection_down", ForgeDirection.DOWN);

    private final String name;
    private final ForgeDirection direction;

    public DirectionalConnectionProperty(String name, ForgeDirection direction) {
        this.name = name;
        this.direction = direction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return Boolean.class;
    }

    @Override
    public boolean hasTrait(BlockPropertyTrait trait) {
        return false;
    }

    @Override
    public Boolean getValue(IBlockAccess world, int x, int y, int z)
    {
        List<IConnectable> connectables = getConnectables(world, x, y, z);
        return !connectables.isEmpty() && IConnectable.getConnection(connectables, world, x, y, z, direction);
    }
}
