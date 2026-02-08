package com.fouristhenumber.utilitiesinexcess.common.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.JCuboidPart;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TSlottedPart;

public class FramePart extends JCuboidPart implements JNormalOcclusion, TSlottedPart {

    public MicroMaterialRegistry.IMicroMaterial material;
    int materialID;

    @Override
    public String getType() {
        return "utilitiesinexcess:frame";
    }

    @Override
    public int getSlotMask() {
        return 1 << 6;
    }

    public void save(NBTTagCompound tag) {
        super.save(tag);
        tag.setString("mat", MicroMaterialRegistry.materialName(materialID));
    }

    public void load(NBTTagCompound tag) {
        super.load(tag);
        this.materialID = MicroMaterialRegistry.materialID(tag.getString("mat"));
    }

    public void writeDesc(MCDataOutput data) {
        data.writeInt(this.materialID);
    }

    public void readDesc(MCDataInput data) {
        this.materialID = data.readInt();
    }

    @Override
    public boolean occlusionTest(TMultiPart part) {
        return NormalOcclusionTest.apply(this, part);
    }

    @Override
    public Cuboid6 getBounds() {
        return null;
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes() {
        double w = 0.25;
        List<Cuboid6> boxes = new ArrayList<>();

        boxes.add(new Cuboid6(0, 0, 0, 1, 1, w));
        boxes.add(new Cuboid6(0, 0, 1 - w, 1, 1, 1));
        boxes.add(new Cuboid6(0, 0, w, w, 1, 1 - w));
        boxes.add(new Cuboid6(1 - w, 0, w, 1, 1, 1 - w));

        return boxes;
    }

    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Arrays.asList(this.getBounds());
    }
}
