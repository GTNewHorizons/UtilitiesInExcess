package com.fouristhenumber.utilitiesinexcess.common.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSpike extends ModelBase {

    public ModelRenderer base;
    public ModelRenderer shape1;
    public ModelRenderer shape1_1;
    public ModelRenderer shape1_2;
    public ModelRenderer shape1_3;
    public ModelRenderer shape1_4;
    public ModelRenderer shape1_5;
    public ModelRenderer shape1_6;
    public ModelRenderer shape1_7;
    public ModelRenderer shape1_8;
    public ModelRenderer shape1_9;
    public ModelRenderer shape1_10;
    public ModelRenderer shape1_11;

    public ModelSpike() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.shape1_3 = new ModelRenderer(this, 0, 17);
        this.shape1_3.setRotationPoint(-7.0F, 22.0F, -3.0F);
        this.shape1_3.addBox(0.0F, 0.0F, 4.0F, 6, 1, 6, 0.0F);
        this.shape1_1 = new ModelRenderer(this, 24, 18);
        this.shape1_1.setRotationPoint(2.0F, 20.0F, -2.0F);
        this.shape1_1.addBox(0.0F, 0.0F, 4.0F, 4, 2, 4, 0.0F);
        this.shape1_6 = new ModelRenderer(this, 0, 17);
        this.shape1_6.setRotationPoint(1.0F, 22.0F, -11.0F);
        this.shape1_6.addBox(0.0F, 0.0F, 4.0F, 6, 1, 6, 0.0F);
        this.shape1 = new ModelRenderer(this, 0, 17);
        this.shape1.setRotationPoint(1.0F, 22.0F, -3.0F);
        this.shape1.addBox(0.0F, 0.0F, 4.0F, 6, 1, 6, 0.0F);
        this.shape1_2 = new ModelRenderer(this, 0, 0);
        this.shape1_2.setRotationPoint(3.0F, 17.0F, -1.0F);
        this.shape1_2.addBox(0.0F, 0.0F, 4.0F, 2, 3, 2, 0.0F);
        this.shape1_4 = new ModelRenderer(this, 24, 18);
        this.shape1_4.setRotationPoint(-6.0F, 20.0F, -2.0F);
        this.shape1_4.addBox(0.0F, 0.0F, 4.0F, 4, 2, 4, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(-8.0F, 23.0F, -8.0F);
        this.base.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16, 0.0F);
        this.shape1_9 = new ModelRenderer(this, 0, 17);
        this.shape1_9.setRotationPoint(-7.0F, 22.0F, -11.0F);
        this.shape1_9.addBox(0.0F, 0.0F, 4.0F, 6, 1, 6, 0.0F);
        this.shape1_11 = new ModelRenderer(this, 0, 0);
        this.shape1_11.setRotationPoint(-5.0F, 17.0F, -9.0F);
        this.shape1_11.addBox(0.0F, 0.0F, 4.0F, 2, 3, 2, 0.0F);
        this.shape1_10 = new ModelRenderer(this, 24, 18);
        this.shape1_10.setRotationPoint(-6.0F, 20.0F, -10.0F);
        this.shape1_10.addBox(0.0F, 0.0F, 4.0F, 4, 2, 4, 0.0F);
        this.shape1_8 = new ModelRenderer(this, 0, 0);
        this.shape1_8.setRotationPoint(3.0F, 17.0F, -9.0F);
        this.shape1_8.addBox(0.0F, 0.0F, 4.0F, 2, 3, 2, 0.0F);
        this.shape1_5 = new ModelRenderer(this, 0, 0);
        this.shape1_5.setRotationPoint(-5.0F, 17.0F, -1.0F);
        this.shape1_5.addBox(0.0F, 0.0F, 4.0F, 2, 3, 2, 0.0F);
        this.shape1_7 = new ModelRenderer(this, 24, 18);
        this.shape1_7.setRotationPoint(2.0F, 20.0F, -10.0F);
        this.shape1_7.addBox(0.0F, 0.0F, 4.0F, 4, 2, 4, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.shape1_3.render(f5);
        this.shape1_1.render(f5);
        this.shape1_6.render(f5);
        this.shape1.render(f5);
        this.shape1_2.render(f5);
        this.shape1_4.render(f5);
        this.base.render(f5);
        this.shape1_9.render(f5);
        this.shape1_11.render(f5);
        this.shape1_10.render(f5);
        this.shape1_8.render(f5);
        this.shape1_5.render(f5);
        this.shape1_7.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
