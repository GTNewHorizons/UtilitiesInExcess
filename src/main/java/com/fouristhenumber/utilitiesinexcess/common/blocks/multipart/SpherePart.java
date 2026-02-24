package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;

import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.SphereRenderingHelper.RenderMicroMaterialSphere;


public class SpherePart extends MaterialBasedPart
{

    public final static String name = "ue_sphere";

    public SpherePart(int material) {
        this.material = material;
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public void render(Vector3 position, int pass)
    {
        if (pass == -1 )
        {
            RenderMicroMaterialSphere(position, pass, getIMaterial(), world());
        }
        else
        {
            RenderMicroMaterialSphere(position, pass, getIMaterial(), world());
        }
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
