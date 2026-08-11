package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

public class PipeCoverPart extends UEMultipart {

    Material material;

    public PipeCoverPart(int materialId) {
        this.material = new Material(materialId);
    }

    @Override
    public String getType() {
        return "ue_pipecover";
    }

    @Override
    public void render(Vector3 position, int pass) {

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

    public void save(NBTTagCompound tag) {
        material.save(tag);
    }

    public void load(NBTTagCompound tag) {
        material.load(tag);
    }

    public void writeDesc(MCDataOutput packet) {
        material.writeDesc(packet);
    }

    public ItemStack pickItem(MovingObjectPosition hit) {
        return UEMultipartItem.createStack(material.id, UiEPartFactory.partMap.get(this.getType()));
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
