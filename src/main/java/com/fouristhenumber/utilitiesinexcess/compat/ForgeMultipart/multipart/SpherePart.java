package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.SphereRenderingHelper.RenderMicroMaterialSphere;

import java.util.Collections;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;

public class SpherePart extends MaterialBasedPart {

    public static final Cuboid6 Bounds = new Cuboid6(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);

    public final static String name = "ue_sphere";

    public SpherePart(int material) {
        this.material = material;
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public void render(Vector3 position, int pass) {
        RenderMicroMaterialSphere(position, pass, getIMaterial(), world());
    }

    @Override
    public Cuboid6 getBounds() {
        return Bounds;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singleton(Bounds);
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes() {
        return Collections.singleton(Bounds);
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        return Collections.singleton(new IndexedCuboid6(0, Bounds));
    }
}
