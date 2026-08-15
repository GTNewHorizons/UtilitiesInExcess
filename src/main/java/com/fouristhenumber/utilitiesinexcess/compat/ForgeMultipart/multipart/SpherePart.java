package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.block.SphereRenderingHelper.RenderMicroMaterialSphere;

import java.util.Collections;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.MicroMaterialRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

public class SpherePart extends UiEMultipart implements IMaterialPart
{

    public static final Cuboid6 Bounds = new Cuboid6(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);

    public final static String name = "ue_sphere";

    public Material material;

    public SpherePart(int materialId) {
        this.material = new Material(materialId);
    }

    public SpherePart(MCDataInput packet)
    {
        this.material = new Material(MicroMaterialRegistry.readMaterialID(packet));
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public void render(Vector3 position, int pass) {
        RenderMicroMaterialSphere(position, pass, material.getIMaterial(), world());
    }

    @Override
    public Cuboid6 getBounds() {
        return Bounds;
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
    public boolean renderStatic(Vector3 position, int pass) {
        if (this.material.canMaterialRenderInPass(pass)) {
            render(position, pass);
            return true;
        }
        return false;
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
        return UiEMultipartMaterialItem.createStack(material.id, UiEPartFactory.partMap.get(this.getType()));
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

    @Override
    public Material getMaterial() {
        return material;
    }
}
