package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraftforge.common.util.ForgeDirection;

public class WallPart extends ConnectablePart
{
    @Override
    public String getType() {
        return "ue_wall";
    }

    @Override
    public void render(Vector3 position, int pass) {

    }

    @Override
    public Cuboid6 getBaseBounds() {
        return null;
    }

    @Override
    public Cuboid6 getConnectionBounds(ForgeDirection side) {
        return null;
    }

    @Override
    public Cuboid6 getBaseCollisionBounds() {
        return null;
    }

    @Override
    public Cuboid6 getCollisionConnectionBounds(ForgeDirection side) {
        return null;
    }

    @Override
    public Cuboid6 getBounds() {
        return null;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return null;
    }
}
