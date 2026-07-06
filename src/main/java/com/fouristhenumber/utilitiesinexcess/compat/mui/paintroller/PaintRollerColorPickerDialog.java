package com.fouristhenumber.utilitiesinexcess.compat.mui.paintroller;

import java.util.function.Consumer;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.HueBar;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.PageButton;
import com.cleanroommc.modularui.widgets.PagedWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost;

// Slightly modified version of ModularUI2's ColorPickerDialog
// Thank you Modular UI <3
public class PaintRollerColorPickerDialog extends Dialog<Integer> {

    private static final IDrawable handleBackground = new Rectangle().color(Color.WHITE.main);

    private int color;
    private int red;
    private int green;
    private int blue;
    private double hue;
    private double saturation;
    private double value;

    private final Rectangle preview = new Rectangle();
    private final Rectangle sliderBackgroundR = new Rectangle();
    private final Rectangle sliderBackgroundG = new Rectangle();
    private final Rectangle sliderBackgroundB = new Rectangle();
    private final Rectangle sliderBackgroundS = new Rectangle();
    private final Rectangle sliderBackgroundV = new Rectangle();

    public PaintRollerColorPickerDialog(Consumer<Integer> resultConsumer, int startColor) {
        this("color_picker", resultConsumer, startColor);
    }

    public PaintRollerColorPickerDialog(String name, Consumer<Integer> resultConsumer, int startColor) {
        super(name, resultConsumer);
        updateAll(startColor);
        size(140, 102).background(GuiTextures.MC_BACKGROUND);
        PagedWidget.Controller controller = new PagedWidget.Controller();
        child(
            new TileEntityTradingPost.HelpWidget().top(2)
                .left(1)
                .size(12)
                .paddingRight(2)
                .tooltipBuilder(PaintRollerColorPickerDialog::buildHelpToolTip));
        child(
            Flow.col()
                .left(5)
                .right(5)
                .top(4)
                .bottom(5)
                .child(
                    IKey.str("Select Color:")
                        .asWidget()
                        .leftRel(0.5f)
                        .anchorLeft(0.5f)
                        .margin(0, 0, 0, 2))
                .child(
                    Flow.row()
                        .left(5)
                        .right(5)
                        .height(14)
                        .child(
                            new PageButton(0, controller).sizeRel(0.5f, 1f)
                                .overlay(IKey.str("HSV")))
                        .child(
                            new PageButton(1, controller).sizeRel(0.5f, 1f)
                                .overlay(IKey.str("RGB"))))
                .child(
                    Flow.row()
                        .widthRel(1f)
                        .height(12)
                        .marginTop(4)
                        .child(
                            IKey.str("Hex: ")
                                .asWidget()
                                .heightRel(1f))
                        .left(5)
                        .child(
                            new TextFieldWidget().size(85, 12)
                                .setValidator(this::validateRawColor)
                                .value(
                                    new StringValue.Dynamic(
                                        () -> "#" + Color.toFullHexString(this.red, this.green, this.blue),
                                        val -> {
                                            try {
                                                updateAll((int) (long) Long.decode(val));
                                            } catch (NumberFormatException ignored) {
                                                ModularUI.LOGGER.error("Illegal color string '{}'", val);
                                            }
                                        })))
                        .child(
                            this.preview.asWidget()
                                .background(GuiTextures.CHECKBOARD)
                                .size(10, 10)
                                .margin(1)))
                .child(
                    new PagedWidget<>().left(5)
                        .right(5)
                        .expanded()
                        .controller(controller)
                        .addPage(createHSVPage())
                        .addPage(createRGBPage()))
                .child(
                    Flow.row()
                        .left(10)
                        .right(10)
                        .height(14)
                        .marginBottom(1)
                        .mainAxisAlignment(Alignment.MainAxis.SPACE_BETWEEN)
                        .child(
                            new ButtonWidget<>().heightRel(1f)
                                .width(50)
                                .overlay(IKey.str("Cancel"))
                                .onMousePressed(button -> {
                                    closeIfOpen();
                                    return true;
                                }))
                        .child(
                            new ButtonWidget<>().heightRel(1f)
                                .width(50)
                                .overlay(IKey.str("Confirm"))
                                .onMousePressed(button -> {
                                    closeWith(this.color);
                                    return true;
                                }))));
    }

    private IWidget createRGBPage() {
        return Flow.col()
            .sizeRel(1f, 1f)
            .child(
                Flow.row()
                    .widthRel(1f)
                    .height(12)
                    .child(
                        IKey.str("R: ")
                            .asWidget()
                            .heightRel(1f))
                    .child(
                        createSlider(this.sliderBackgroundR).name("red")
                            .bounds(0, 31)
                            .value(
                                new DoubleValue.Dynamic(() -> this.red >> 3, red -> this.updateRed(((int) red) << 3)))))
            .child(
                Flow.row()
                    .widthRel(1f)
                    .height(12)
                    .child(
                        IKey.str("G: ")
                            .asWidget()
                            .heightRel(1f))
                    .child(
                        createSlider(this.sliderBackgroundG).name("green")
                            .bounds(0, 31)
                            .value(
                                new DoubleValue.Dynamic(
                                    () -> this.green >> 3,
                                    green -> this.updateGreen(((int) green) << 3)))))
            .child(
                Flow.row()
                    .widthRel(1f)
                    .height(12)
                    .child(
                        IKey.str("B: ")
                            .asWidget()
                            .heightRel(1f))
                    .child(
                        createSlider(this.sliderBackgroundB).name("blue")
                            .bounds(0, 31)
                            .value(
                                new DoubleValue.Dynamic(
                                    () -> this.blue >> 3,
                                    blue -> this.updateBlue(((int) blue) << 3)))));
    }

    private IWidget createHSVPage() {
        return Flow.col()
            .sizeRel(1f, 1f)
            .child(
                Flow.row()
                    .widthRel(1f)
                    .height(12)
                    .child(
                        IKey.str("H: ")
                            .asWidget()
                            .heightRel(1f))
                    .child(
                        createSlider(new HueBar(GuiAxis.X)).name("hue")
                            .bounds(0, 360)
                            .value(new DoubleValue.Dynamic(() -> this.hue, this::updateHue))))
            .child(
                Flow.row()
                    .widthRel(1f)
                    .height(12)
                    .child(
                        IKey.str("S: ")
                            .asWidget()
                            .heightRel(1f))
                    .child(
                        createSlider(this.sliderBackgroundS).name("saturation")
                            .bounds(0, 1)
                            .value(new DoubleValue.Dynamic(() -> this.saturation, this::updateSaturation))))
            .child(
                Flow.row()
                    .widthRel(1f)
                    .height(12)
                    .child(
                        IKey.str("V: ")
                            .asWidget()
                            .heightRel(1f))
                    .child(
                        createSlider(this.sliderBackgroundV).name("value")
                            .bounds(0, 1)
                            .value(new DoubleValue.Dynamic(() -> this.value, this::updateValue))));
    }

    private static SliderWidget createSlider(IDrawable background) {
        return new SliderWidget().expanded()
            .heightRel(1f)
            .background(
                background.asIcon()
                    .size(0, 4))
            .sliderTexture(handleBackground)
            .sliderSize(2, 8);
    }

    private String validateRawColor(String raw) {
        if (!raw.startsWith("#")) {
            if (raw.startsWith("0x") || raw.startsWith("0X")) {
                raw = raw.substring(2);
            }
            return "#" + raw;
        }
        return raw;
    }

    private void updateRed(double v) {
        this.red = (int) v;
        updateFromRGB();
    }

    private void updateGreen(double v) {
        this.green = (int) v;
        updateFromRGB();
    }

    private void updateBlue(double v) {
        this.blue = (int) v;
        updateFromRGB();
    }

    private void updateHue(double v) {
        this.hue = v;
        updateFromHSV();
    }

    private void updateSaturation(double v) {
        this.saturation = v;
        updateFromHSV();
    }

    private void updateValue(double v) {
        this.value = v;
        updateFromHSV();
    }

    private void updateFromRGB() {
        this.color = Color.rgb(this.red, this.green, this.blue);
        this.saturation = Color.getHSVSaturation(this.color);
        this.value = Color.getValue(this.color);
        this.hue = Color.getHue(this.color);
        updateColor(this.color);
    }

    private void updateFromHSV() {
        this.color = Color.ofHSV((float) this.hue, (float) this.saturation, (float) this.value);
        this.red = Color.getRed(this.color) & 0b11111000;
        this.green = Color.getGreen(this.color) & 0b11111000;
        this.blue = Color.getBlue(this.color) & 0b11111000;
        updateColor(this.color);
    }

    public void updateAll(int color) {
        this.color = color & 0b00000000_11111000_11111000_11111000;
        this.red = Color.getRed(this.color);
        this.green = Color.getGreen(this.color);
        this.blue = Color.getBlue(this.color);
        this.hue = Color.getHue(this.color);
        this.saturation = Color.getHSVSaturation(this.color);
        this.value = Color.getValue(this.color);
        updateColor(this.color);
    }

    public void updateColor(int color) {
        color = Color.withAlpha(color, 255);
        int rs = Color.withRed(color, 0), re = Color.withRed(color, 255);
        int gs = Color.withGreen(color, 0), ge = Color.withGreen(color, 255);
        int bs = Color.withBlue(color, 0), be = Color.withBlue(color, 255);
        this.sliderBackgroundR.horizontalGradient(rs, re);
        this.sliderBackgroundG.horizontalGradient(gs, ge);
        this.sliderBackgroundB.horizontalGradient(bs, be);
        this.sliderBackgroundS
            .horizontalGradient(Color.withHSVSaturation(color, 0f), Color.withHSVSaturation(color, 1f));
        this.sliderBackgroundV.horizontalGradient(Color.withValue(color, 0f), Color.withValue(color, 1f));
        this.preview.color(color);
    }

    public static void buildHelpToolTip(RichTooltip tooltip) {
        tooltip.addLine(StatCollector.translateToLocal("item.paint_roller.help_tooltip.0"));
        tooltip.addLine(StatCollector.translateToLocal("item.paint_roller.help_tooltip.1"));
        tooltip.addLine(StatCollector.translateToLocal("item.paint_roller.help_tooltip.2"));
        tooltip.addLine(StatCollector.translateToLocal("item.paint_roller.help_tooltip.3"));
    }
}
