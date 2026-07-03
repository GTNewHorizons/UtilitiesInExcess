package com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Interpolation;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.Expandable;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityFilingCabinet;

import cpw.mods.fml.common.registry.GameData;

public class CabinetSettingsTab {

    static final int TAB_SIZE = 26;
    private static final int BUTTON_SIZE = 16;
    static final int GAP = 2;
    private static final int BUTTON_TOP = (TAB_SIZE - BUTTON_SIZE) / 2;

    private final TileEntityFilingCabinet tile;
    private Runnable onChanged;

    private SortType sortType = SortType.BY_NAME;
    private ScrollDirection scrollDirection = ScrollDirection.VERTICAL;
    private SlotDirection slotDirection = SlotDirection.HORIZONTAL;

    public CabinetSettingsTab(TileEntityFilingCabinet tile) {
        this.tile = tile;
    }

    public void onChanged(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public SortType getSortType() {
        return sortType;
    }

    public ScrollDirection getScrollDirection() {
        return scrollDirection;
    }

    public SlotDirection getSlotDirection() {
        return slotDirection;
    }

    public void addTo(ModularPanel panel) {
        int firstButtonX = TAB_SIZE + GAP;
        CycleButtonWidget sortButton = button(SortType.class, SortType.values(), () -> sortType, v -> sortType = v)
            .pos(firstButtonX, BUTTON_TOP);
        CycleButtonWidget scrollButton = button(
            ScrollDirection.class,
            ScrollDirection.values(),
            () -> scrollDirection,
            v -> scrollDirection = v).pos(firstButtonX + (BUTTON_SIZE + GAP), BUTTON_TOP);
        CycleButtonWidget slotButton = button(
            SlotDirection.class,
            SlotDirection.values(),
            () -> slotDirection,
            v -> slotDirection = v).pos(firstButtonX + 2 * (BUTTON_SIZE + GAP), BUTTON_TOP);

        int expandedWidth = TAB_SIZE + GAP + 3 * (BUTTON_SIZE + GAP) + 3;

        Expandable widget = new Expandable().name("cabinet_settings")
            .leftRelOffset(1f, 1)
            .background(GuiTextures.MC_BACKGROUND)
            .excludeAreaInRecipeViewer()
            .stencilTransform((r, expanded) -> {
                r.width = Math.max(TAB_SIZE, r.width - 5);
                r.height = Math.max(TAB_SIZE, r.height - 5);
            })
            .animationDuration(300)
            .interpolation(Interpolation.SINE_OUT)
            .collapsedView(
                GuiTextures.GEAR.asIcon()
                    .asWidget()
                    .size(TAB_SIZE)
                    .pos(0, 0))
            .expandedView(
                new ParentWidget<>().name("cabinet_settings_open")
                    .child(
                        GuiTextures.GEAR.asIcon()
                            .asWidget()
                            .size(TAB_SIZE)
                            .pos(0, 0))
                    .child(sortButton)
                    .child(scrollButton)
                    .child(slotButton)
                    .size(expandedWidth, TAB_SIZE));

        panel.child(widget);
    }

    private <E extends Enum<E> & Setting<E>> CycleButtonWidget button(Class<E> enumClass, E[] values,
        Supplier<E> getter, Consumer<E> setter) {
        EnumSyncValue<E, ?> value = new EnumSyncValue<>(enumClass, getter, v -> {
            setter.accept(v);
            if (tile.getWorldObj() == null || !tile.getWorldObj().isRemote) {
                tile.sortInventory();
            }
            tile.markDirty();
            if (onChanged != null) onChanged.run();
        });
        value.allowC2S(true);

        CycleButtonWidget button = new CycleButtonWidget().value(value)
            .size(BUTTON_SIZE);
        for (int i = 0; i < values.length; i++) {
            E v = values[i];
            button.stateOverlay(i, v.icon());
            button.tooltipBuilder(i, tt -> tt.addLine(v.tooltip()));
        }
        return button;
    }

    public static void writeToNBT(NBTTagCompound tag, CabinetSettingsTab settings) {
        writeNBT(tag, settings.sortType);
        writeNBT(tag, settings.scrollDirection);
        writeNBT(tag, settings.slotDirection);
    }

    public static void readFromNBT(NBTTagCompound tag, CabinetSettingsTab settings) {
        settings.sortType = readNBT(tag, SortType.values(), settings.sortType);
        settings.scrollDirection = readNBT(tag, ScrollDirection.values(), settings.scrollDirection);
        settings.slotDirection = readNBT(tag, SlotDirection.values(), settings.slotDirection);
    }

    private static <E extends Enum<E> & Setting<E>> void writeNBT(NBTTagCompound tag, E value) {
        tag.setByte(value.nbtKey(), (byte) value.ordinal());
    }

    private static <E extends Enum<E> & Setting<E>> E readNBT(NBTTagCompound tag, E[] values, E fallback) {
        String key = fallback.nbtKey();
        if (!tag.hasKey(key)) return fallback;
        int index = tag.getByte(key);
        return (index >= 0 && index < values.length) ? values[index] : fallback;
    }

    static IDrawable settingIcon(String name) {
        return UITexture.fullImage(UtilitiesInExcess.MODID, "gui/cabinet/settings/" + name)
            .asIcon()
            .size(12);
    }

    public interface Setting<S extends Enum<S> & Setting<S>> {

        String nbtKey();

        String titleKey();

        String labelKey();

        IDrawable icon();

        default String tooltip() {
            return StatCollector.translateToLocal(titleKey()) + ": " + StatCollector.translateToLocal(labelKey());
        }
    }

    public enum SortType implements Setting<SortType> {

        BY_NAME("name", byText(SortType::displayName)),
        BY_MOD_ID("mod_id", byText(SortType::modId)),
        BY_COUNT("count", Comparator.comparing(SortType::countOrNull, Comparator.nullsLast(Comparator.reverseOrder()))),
        BY_ORE_DICT("ore_dict", byText(SortType::oreDict));

        private final String label;
        private final IDrawable icon;
        private final Comparator<ItemStack> comparator;

        SortType(String label, Comparator<ItemStack> primary) {
            this.label = label;
            this.icon = settingIcon("sort_" + label);
            this.comparator = primary.thenComparing(byText(SortType::modId))
                .thenComparingInt(SortType::countInt)
                .thenComparing(byText(SortType::oreDict))
                .thenComparing(byText(ItemStack::getDisplayName));
        }

        public Comparator<ItemStack> stackComparator() {
            return comparator;
        }

        private static Comparator<ItemStack> byText(Function<ItemStack, String> key) {
            return Comparator.comparing(
                stack -> (stack == null || stack.getItem() == null) ? null : key.apply(stack),
                Comparator.nullsLast(Comparator.naturalOrder()));
        }

        private static Integer countOrNull(ItemStack stack) {
            return stack == null ? null : stack.stackSize;
        }

        private static int countInt(ItemStack stack) {
            return stack == null ? 0 : stack.stackSize;
        }

        @Override
        public IDrawable icon() {
            return icon;
        }

        @Override
        public String nbtKey() {
            return "SortType";
        }

        @Override
        public String titleKey() {
            return "gui.cabinet.sort.title";
        }

        @Override
        public String labelKey() {
            return "gui.cabinet.sort." + label;
        }

        public static String displayName(ItemStack stack) {
            return (stack == null || stack.getItem() == null) ? null : stack.getDisplayName();
        }

        public static String modId(ItemStack stack) {
            String id = GameData.getItemRegistry()
                .getNameForObject(stack.getItem());
            if (id == null) return "unknown";
            int i = id.indexOf(":");
            return i == -1 ? "minecraft" : id.substring(0, i);
        }

        public static String oreDict(ItemStack stack) {
            int[] ids = OreDictionary.getOreIDs(stack);
            return ids.length == 0 ? "" : OreDictionary.getOreName(ids[0]);
        }
    }

    public enum ScrollDirection implements Setting<ScrollDirection> {

        HORIZONTAL("horizontal"),
        VERTICAL("vertical");

        private final String label;
        private final IDrawable icon;

        ScrollDirection(String label) {
            this.label = label;
            this.icon = settingIcon("scroll_" + label);
        }

        public boolean isHorizontal() {
            return this == HORIZONTAL;
        }

        @Override
        public IDrawable icon() {
            return icon;
        }

        @Override
        public String nbtKey() {
            return "ScrollDir";
        }

        @Override
        public String titleKey() {
            return "gui.cabinet.scroll.title";
        }

        @Override
        public String labelKey() {
            return "gui.cabinet.scroll." + label;
        }
    }

    public enum SlotDirection implements Setting<SlotDirection> {

        HORIZONTAL("horizontal"),
        VERTICAL("vertical");

        private final String label;
        private final IDrawable icon;

        SlotDirection(String label) {
            this.label = label;
            this.icon = settingIcon("slot_" + label);
        }

        public boolean isHorizontal() {
            return this == HORIZONTAL;
        }

        @Override
        public IDrawable icon() {
            return icon;
        }

        @Override
        public String nbtKey() {
            return "SlotDir";
        }

        @Override
        public String titleKey() {
            return "gui.cabinet.slot_dir.title";
        }

        @Override
        public String labelKey() {
            return "gui.cabinet.slot_dir." + label;
        }
    }
}
