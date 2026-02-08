package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

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
}
