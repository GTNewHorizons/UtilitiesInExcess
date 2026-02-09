package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;

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
    public Cuboid6 getConnectionBounds(int side) {
        return null;
    }

    @Override
    public Cuboid6 getBaseCollisionBounds() {
        return null;
    }

    @Override
    public Cuboid6 getCollisionConnectionBounds(int side) {
        return null;
    }
}
