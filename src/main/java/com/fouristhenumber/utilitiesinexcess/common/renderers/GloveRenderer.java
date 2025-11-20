package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorClientMinecraft;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorItemRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.RenderableCube;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GloveRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
        final ItemRendererHelper helper) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (stack == null) return;

        if (!(type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED)) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) return;
        switch (type) {
            case EQUIPPED_FIRST_PERSON:
                renderHand(((AccessorClientMinecraft) mc).timer().renderPartialTicks);
                break;
            case EQUIPPED:
                break;
        }
    }

    public static final RenderableCube cube = new RenderableCube(
        0,
        0,
        0,
        1,
        1,
        1,
        new float[][] { { 8, 8, 16, 16 }, { 16, 8, 24, 16 }, { 0, 8, 8, 16 }, { 8, 0, 16, 8 }, { 0, 0, 8, 8 },
            { 16, 0, 24, 8 } });
    public static final ResourceLocation gloveTexture = new ResourceLocation(
        "utilitiesinexcess",
        "textures/items/glove_model.png");

    public static void renderGloveAsBauble() {
        int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        // 0.27 -0.73 1.43 -0.5 0 0 0 1
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glScalef(0.27F, 0.27F, 0.27F);
        GL11.glTranslatef(-0.73F, 1.43F, -0.5F);

        mc.renderEngine.bindTexture(gloveTexture);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        cube.draw(t, 0, 0, 0, 24);
        t.draw();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);
    }

    public static void renderHand(float renderPartialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP entityclientplayermp = mc.thePlayer;
        EntityPlayerSP entityplayersp = (EntityPlayerSP) entityclientplayermp;
        AccessorItemRenderer air = (AccessorItemRenderer) mc.entityRenderer.itemRenderer;

        float f13, f5, f6, f7, f10;
        f5 = entityclientplayermp.getSwingProgress(renderPartialTicks);
        float f1 = f5 > 0 ? 1
            : air.prevEquippedProgress() + (air.equippedProgress() - air.prevEquippedProgress()) * renderPartialTicks;

        // Surely this will cause absolutely no problems at all :)
        // (I checked, it won't. Added methods where each popped matrix is pushed)
        GL11.glPopMatrix(); // ForgeHooksClient.renderEquippedItem
        GL11.glPopMatrix(); // ItemRenderer.renderItem
        GL11.glPopMatrix(); // ItemRenderer.renderItemInFirstPerson
        GL11.glPushMatrix();

        float f3 = entityplayersp.prevRenderArmPitch
            + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * renderPartialTicks;
        float f4 = entityplayersp.prevRenderArmYaw
            + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * renderPartialTicks;
        GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);

        f13 = 0.8F;
        f6 = MathHelper.sin(f5 * (float) Math.PI);
        f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
        GL11.glTranslatef(
            -f7 * 0.3F,
            MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI * 2.0F) * 0.4F,
            -f6 * 0.4F);
        GL11.glTranslatef(0.8F * f13, -0.75F * f13 - (1.0F - f1) * 0.6F, -0.9F * f13);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        f5 = entityclientplayermp.getSwingProgress(renderPartialTicks);
        f6 = MathHelper.sin(f5 * f5 * (float) Math.PI);
        f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
        GL11.glRotatef(f7 * 70.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f6 * 20.0F, 0.0F, 0.0F, 1.0F);
        mc.getTextureManager()
            .bindTexture(entityclientplayermp.getLocationSkin());
        GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
        GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(5.6F, 0.0F, 0.0F);
        f10 = 1.0F;
        GL11.glScalef(f10, f10, f10);
        ((RenderPlayer) RenderManager.instance.getEntityRenderObject(entityclientplayermp))
            .renderFirstPersonArm(entityclientplayermp);

        // Drawn in a mixin now
        // 0.27 -1.8 1.95 -0.48 6.2 0 0 1
        // GL11.glScalef(0.27F, 0.27F, 0.27F);
        // GL11.glRotatef(6.2F, 0F, 0F, 1F);
        // GL11.glTranslatef(-1.8F, 1.95F, -0.48F);
        //
        // mc.renderEngine.bindTexture(gloveTexture);
        // Tessellator t = Tessellator.instance;
        // t.startDrawingQuads();
        // cube.draw(t, 0, 0, 0, 24);
        // t.draw();

        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glPushMatrix();
        GL11.glPushMatrix();
    }
}
