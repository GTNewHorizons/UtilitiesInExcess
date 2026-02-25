package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;

public class PipeCoverPart extends MaterialBasedPart {

    @Override
    public String getType() {
        return "ue_pipecover";
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
        return null;
    }
}
