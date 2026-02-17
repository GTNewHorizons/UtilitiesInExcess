package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraftforge.common.util.ForgeDirection;

public class WallPart extends ConnectablePart
{

    public static final String name = "ue_wall";

    public WallPart(int material, int side) {
        super(side);
        this.material = material;
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public boolean renderStatic(Vector3 position, int pass)
    {
        if (getIMaterial().canRenderInPass(pass))
        {
            render(position, pass);
            return true;
        }
        return false;
    }

    @Override
    public void render(Vector3 position, int pass)
    {

    }

    @Override
    public Cuboid6 getBounds()
    {
        return null;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return null;
    }

    @Override
    public Cuboid6 getConnectionInDirection(ForgeDirection side) {
        return null;
    }
}
