package com.fouristhenumber.utilitiesinexcess.compat.mui;

import java.util.function.Supplier;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Area;

public class EntityDisplay extends Widget<EntityDisplay> {

    private final Supplier<EntityLivingBase> entitySupplier;
    private final boolean lookAtMouse;
    private final float mouseFollowStrength;

    public EntityDisplay(Supplier<EntityLivingBase> e, boolean lookAtMouse, float mouseFollowStrength) {
        this.entitySupplier = e;
        this.lookAtMouse = lookAtMouse;
        this.mouseFollowStrength = mouseFollowStrength;
        this.size(18, 36);
    }

    public static void drawEntity(int x, int y, int scale, EntityLivingBase e, boolean lookAtMouse, float mouseX,
        float mouseY, float mouseFollowStrength) {
        if (e == null) return;
        GL11.glColor4f(1, 1, 1, 1);
        Platform.setupDrawItem();
        float yawX = 0;
        float pitchY = 0;
        if (lookAtMouse) {
            yawX = -mouseX;
            pitchY -= mouseY;
            yawX /= scale;
            pitchY /= scale;
            yawX *= mouseFollowStrength;
            pitchY *= mouseFollowStrength;
        }
        // TODO: figure out if u can somehow turn off shadows without mixins
        GuiInventory.func_147046_a(x, y, scale, yawX, pitchY, e);
        Platform.endDrawItem();
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
        EntityLivingBase e = entitySupplier.get();
        Area area = this.getArea();
        drawEntity(
            +area.width / 2,
            area.height,
            area.width,
            e,
            this.lookAtMouse,
            context.getMouseX(),
            context.getMouseY(),
            mouseFollowStrength);
    }
}
