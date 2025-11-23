package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

public class ModelPartRenderHelper {

    // from ModelBiped.render
    public static <T> void renderBipedPart(float p_78785_1_, ModelRenderer model, Runnable renderer) {

        if (model.isHidden) return;
        if (!model.showModel) return;

        GL11.glTranslatef(model.offsetX, model.offsetY, model.offsetZ);

        if (model.rotateAngleX == 0.0F && model.rotateAngleY == 0.0F && model.rotateAngleZ == 0.0F) {
            if (model.rotationPointX == 0.0F && model.rotationPointY == 0.0F && model.rotationPointZ == 0.0F) {
                // GL11.glCallList(model.displayList);
                renderer.run();
            } else {
                GL11.glTranslatef(
                    model.rotationPointX * p_78785_1_,
                    model.rotationPointY * p_78785_1_,
                    model.rotationPointZ * p_78785_1_);
                // GL11.glCallList(model.displayList);
                renderer.run();
                GL11.glTranslatef(
                    -model.rotationPointX * p_78785_1_,
                    -model.rotationPointY * p_78785_1_,
                    -model.rotationPointZ * p_78785_1_);
            }
        } else {
            GL11.glPushMatrix();
            GL11.glTranslatef(
                model.rotationPointX * p_78785_1_,
                model.rotationPointY * p_78785_1_,
                model.rotationPointZ * p_78785_1_);

            if (model.rotateAngleZ != 0.0F) {
                GL11.glRotatef(model.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (model.rotateAngleY != 0.0F) {
                GL11.glRotatef(model.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (model.rotateAngleX != 0.0F) {
                GL11.glRotatef(model.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }

            // GL11.glCallList(model.displayList);
            renderer.run();

            GL11.glPopMatrix();
        }

        GL11.glTranslatef(-model.offsetX, -model.offsetY, -model.offsetZ);
    }
}
