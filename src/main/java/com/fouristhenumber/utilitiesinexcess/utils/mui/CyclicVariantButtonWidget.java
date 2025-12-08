package com.fouristhenumber.utilitiesinexcess.utils.mui;

import java.util.List;
import java.util.function.Consumer;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.ButtonWidget;

/**
 * A specialized button widget that cycles through a list of visual and tooltip variants
 * when pressed. This is useful for toggling between multiple configuration states.
 *
 * <p>
 * Original source:
 * {@code https://github.com/CleanroomMC/RetroSophisticatedBackpack/blob/master/src/main/kotlin/com/cleanroommc/retrosophisticatedbackpacks/client/gui/widgets/CyclicVariantButtonWidget.kt}
 * <br>
 * Ported and adapted from Kotlin to Java for Utilities In Excess.
 * </p>
 */
public class CyclicVariantButtonWidget extends ButtonWidget<CyclicVariantButtonWidget> {

    private final List<Variant> variants;
    private int index;
    private int iconOffset;
    private int iconSize;
    private boolean inEffect = true;

    private final Consumer<Integer> mousePressedUpdater;

    public CyclicVariantButtonWidget(List<Variant> variants, int index, int iconOffset, int iconSize,
        Consumer<Integer> mousePressedUpdater) {
        this.variants = variants;
        this.index = index;
        this.iconOffset = iconOffset;
        this.iconSize = iconSize;
        this.mousePressedUpdater = mousePressedUpdater;

        // Setup size and mouse press behavior
        this.size(20, 20);
        IKey name = variants.get(this.index)
            .getName();

        this.name(name.get());

        this.onMousePressed((button) -> {
            if (button == 1) {
                this.index = (this.index - 1 + variants.size()) % variants.size();
            } else {
                this.index = (this.index + 1) % variants.size();
            }

            mousePressedUpdater.accept(this.index);
            this.markTooltipDirty();
            return true;
        });

        this.tooltipDynamic((tooltip) -> {
            tooltip.addLine(name);

            if (!inEffect) {
                tooltip.addLine(
                    IKey.lang("gui.not_in_effect")
                        .style(IKey.RED));
            }

            tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
        });
    }

    public CyclicVariantButtonWidget(List<Variant> variants, Consumer<Integer> mousePressedUpdater) {
        this(variants, 0, 2, 16, mousePressedUpdater);
    }

    @Override
    public void drawOverlay(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawOverlay(context, widgetTheme);

        IDrawable drawable = variants.get(index)
            .getDrawable();
        drawable.draw(context, iconOffset, iconOffset, iconSize, iconSize, widgetTheme.getTheme());
    }

    public int getIndex() {
        return index;
    }

    public boolean isInEffect() {
        return inEffect;
    }

    public void setInEffect(boolean inEffect) {
        this.inEffect = inEffect;
    }

    public int getIconOffset() {
        return iconOffset;
    }

    public void setIconOffset(int iconOffset) {
        this.iconOffset = iconOffset;
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    // Inner class for variant
    public static class Variant {

        private final IKey name;
        private final IDrawable drawable;

        public Variant(IKey name, IDrawable drawable) {
            this.name = name;
            this.drawable = drawable;
        }

        public IKey getName() {
            return name;
        }

        public IDrawable getDrawable() {
            return drawable;
        }
    }
}
