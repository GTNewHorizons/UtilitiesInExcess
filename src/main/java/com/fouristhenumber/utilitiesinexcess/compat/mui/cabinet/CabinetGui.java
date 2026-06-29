package com.fouristhenumber.utilitiesinexcess.compat.mui.cabinet;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Interpolation;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.Expandable;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting.ScrollDirection;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting.SlotDirection;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting.SortType;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileEntityFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.github.bsideup.jabel.Desugar;

public class CabinetGui {

    public static final UITexture STORAGE_PROGRESS = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/cabinet/progress_storage")
        .adaptable(1)
        .imageSize(32, 8)
        .build();

    public static final UITexture SLOT_PROGRESS = UITexture.builder()
        .location(UtilitiesInExcess.MODID, "gui/cabinet/progress_slot_used")
        .adaptable(1)
        .imageSize(32, 8)
        .build();

    private static final int SLOT_SIZE = 18;
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 5;
    private static final int VERTICAL_WIDTH_ADJUST = -8;
    private static final int HORIZONTAL_HEIGHT_ADJUST = 10;
    private static final int GRID_MARGIN_TOP = 20;

    private static final int UPGRADE_TAB_TOP_COLLAPSED = 22;
    private static final int UPGRADE_TAB_TOP_EXPANDED = 56;

    private final TileEntityFilingCabinet tile;

    private CycleButtonWidget sortButton;
    private CycleButtonWidget scrollButton;
    private CycleButtonWidget slotDirButton;

    private Expandable settingsTab;
    private Expandable upgradeTab;

    public CabinetGui(TileEntityFilingCabinet tile) {
        this.tile = tile;
    }

    public ModularPanel build(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        CabinetSyncValues sync = setupSyncValues(syncManager);

        DynamicSyncHandler gridRefresher = new DynamicSyncHandler().allowC2S(true);
        Runnable triggerRefresh = () -> gridRefresher.notifyUpdate(buf -> {
            buf.writeInt(tile.sortType.ordinal());
            buf.writeInt(tile.scrollDirection.ordinal());
            buf.writeInt(tile.slotDirection.ordinal());
        });

        List<ItemSlot> itemSlotList = createSlotWidgets(syncManager, triggerRefresh);

        ModularPanel panel = new ModularPanel("Cabinet");
        panel.size(176, 217);
        panel.bindPlayerInventory();

        panel.child(buildItemGrid(gridRefresher, itemSlotList));
        panel.child(buildSettingsTab(triggerRefresh));
        panel.child(buildUpgradeTab(syncManager));
        buildStatusBars(panel, sync);
        panel.child(buildTitle());

        triggerRefresh.run();
        return panel;
    }

    private CabinetSyncValues setupSyncValues(PanelSyncManager syncManager) {
        IntSyncValue capacity = new IntSyncValue(() -> tile.inventory.getCapacity());
        IntSyncValue stored = new IntSyncValue(() -> tile.inventory.getStoredQuantity());
        IntSyncValue slots = new IntSyncValue(() -> tile.inventory.getSlots());
        IntSyncValue slotsUsed = new IntSyncValue(() -> tile.inventory.getSlotsUsed());

        syncManager.syncValue("capacity", capacity);
        syncManager.syncValue("stored", stored);
        syncManager.syncValue("slots", slots);
        syncManager.syncValue("slotsUsed", slotsUsed);

        return new CabinetSyncValues(capacity, stored, slots, slotsUsed);
    }

    private List<ItemSlot> createSlotWidgets(PanelSyncManager syncManager, Runnable triggerRefresh) {
        SlotGroup connected = new SlotGroup(
            "cabinet_inventory",
            tile.scrollDirection.getRowSize(tile.inventory.getSlots()),
            true);
        syncManager.registerSlotGroup(connected);

        List<ItemSlot> itemSlotList = new ArrayList<>();
        for (int i = 0; i < tile.inventory.getSlots(); i++) {
            int finalI = i;
            ItemSlot slotWidget = new ItemSlot().syncHandler(
                syncManager.getOrCreateSyncHandler(
                    "slot_" + i,
                    i,
                    ItemSlotSH.class,
                    () -> new ItemSlotSH(
                        new ModularSlot(tile.inventory, finalI).slotGroup(connected)
                            .ignoreMaxStackSize(true)
                            .changeListener((newItem, onlyAmountChanged, client, init) -> {
                                tile.inventory.updateCounts();
                                triggerRefresh.run();
                            }))));
            itemSlotList.add(slotWidget);
        }
        return itemSlotList;
    }

    private IWidget buildItemGrid(DynamicSyncHandler gridRefresher, List<ItemSlot> itemSlotList) {
        gridRefresher.widgetProvider((sm, buf) -> {
            int sortOrd = buf.readInt();
            int scrollOrd = buf.readInt();
            int slotOrd = buf.readInt();

            SortType sort = SortType.values()[sortOrd];
            ScrollDirection scroll = ScrollDirection.values()[scrollOrd];
            SlotDirection slotDir = SlotDirection.values()[slotOrd];

            itemSlotList.sort((slotA, slotB) -> {
                ItemStack stackA = slotA.getSlot()
                    .getStack();
                ItemStack stackB = slotB.getSlot()
                    .getStack();
                return sort.stackComparator()
                    .compare(stackA, stackB);
            });

            int rowSize = scroll.getRowSize(tile.inventory.getSlots());
            int gridWidth = SLOT_SIZE * GRID_COLS + (!scroll.isHorizontal() ? VERTICAL_WIDTH_ADJUST : 0);
            int gridHeight = SLOT_SIZE * GRID_ROWS + (scroll.isHorizontal() ? HORIZONTAL_HEIGHT_ADJUST : 0);

            int slotCount = itemSlotList.size();
            int rows = (int) Math.ceil((double) slotCount / rowSize);
            boolean slotsHorizontal = slotDir.isHorizontal();


            return new Grid().name("Grid")
                .scrollable(scroll.getScrollData())
                .width(gridWidth)
                .height(gridHeight)
                .grid(Grid.createGridOfWidthHeight(rowSize, rows, (col, row, index) -> {
                    int i = slotsHorizontal ? index : col * rows + row;
                    return i < slotCount ? itemSlotList.get(i) : null;
                }));
        });

        DynamicSyncedWidget<?> dynamicGridWidget = new DynamicSyncedWidget<>();
        dynamicGridWidget.syncHandler(gridRefresher);
        dynamicGridWidget.relativeToParent()
            .leftRel(0.5f)
            .anchorLeft(0.5f)
            .marginTop(GRID_MARGIN_TOP)
            .coverChildren();
        return dynamicGridWidget;
    }

    private IWidget buildSettingsTab(Runnable triggerRefresh) {
        createSettingButtons(triggerRefresh);

        settingsTab = new Expandable() {

            @Override
            public Expandable expanded(boolean expanded) {
                Expandable result = super.expanded(expanded);
                if (upgradeTab != null) upgradeTab.scheduleResize();
                return result;
            }
        }.name("expandable")
            .top(0)
            .leftRelOffset(1f, 1)
            .background(GuiTextures.MC_BACKGROUND)
            .excludeAreaInRecipeViewer()
            .stencilTransform((r, expanded) -> {
                r.width = Math.max(20, r.width - 5);
                r.height = Math.max(20, r.height - 5);
            })
            .animationDuration(500)
            .interpolation(Interpolation.BOUNCE_OUT)
            .collapsedView(
                GuiTextures.GEAR.asIcon()
                    .asWidget()
                    .size(20)
                    .pos(0, 0))
            .expandedView(
                new ParentWidget<>().name("settings tab")
                    .child(
                        GuiTextures.GEAR.asIcon()
                            .asWidget()
                            .size(20)
                            .pos(0, -20))
                    .child(sortButton)
                    .child(scrollButton)
                    .child(slotDirButton)
                    .size(20, 54)
                    .margin(0, 0, 20, 5));
        return settingsTab;
    }

    private IWidget buildUpgradeTab(PanelSyncManager syncManager) {
        SlotGroup upgradeGroup = new SlotGroup("cabinet_upgrades", 1);
        syncManager.registerSlotGroup(upgradeGroup);

        ItemSlot upgradeSlot = new ItemSlot().slot(new ModularSlot(tile.upgradeInventory, 0).slotGroup(upgradeGroup))
            .tooltipDynamic(
                tt -> tt.addLine(
                    StatCollector.translateToLocalFormatted(
                        "gui.cabinet.upgrades",
                        tile.getNumberOfUpgrades(),
                        FilingCabinetsConfig.upgradeCountMax)));

        upgradeTab = new Expandable().name("upgrades expandable")
            .top(
                () -> settingsTab != null && settingsTab.isExpanded() ? UPGRADE_TAB_TOP_EXPANDED
                    : UPGRADE_TAB_TOP_COLLAPSED,
                Unit.Measure.PIXEL)
            .leftRelOffset(1f, 1)
            .background(GuiTextures.MC_BACKGROUND)
            .excludeAreaInRecipeViewer()
            .stencilTransform((r, expanded) -> {
                r.width = Math.max(20, r.width - 5);
                r.height = Math.max(20, r.height - 5);
            })
            .animationDuration(500)
            .interpolation(Interpolation.BOUNCE_OUT)
            .collapsedView(
                new ItemDrawable(tile.getUpgradeStack(1)).asWidget()
                    .size(16)
                    .pos(2, 2))
            .expandedView(
                new ParentWidget<>().name("upgrades tab")
                    .child(
                        new ItemDrawable(tile.getUpgradeStack(1)).asWidget()
                            .size(16)
                            .pos(2, -20))
                    .child(upgradeSlot.pos(1, 1))
                    .size(20, 20)
                    .margin(0, 0, 20, 5));
        return upgradeTab;
    }

    private void buildStatusBars(ModularPanel panel, CabinetSyncValues sync) {
        panel.child(
            new ProgressWidget()
                .value(new DoubleSyncValue(() -> (double) tile.inventory.getSlotsUsed() / tile.inventory.getSlots()))
                .texture(SLOT_PROGRESS, 4)
                .direction(ProgressWidget.Direction.RIGHT)
                .size(32, 4)
                .leftRel(1f)
                .anchorLeft(1f)
                .marginTop(6)
                .marginRight(7)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "gui.cabinet.slot",
                            formatNumber(
                                sync.slotsUsed()
                                    .getIntValue()),
                            formatNumber(
                                sync.slots()
                                    .getIntValue())))));

        panel.child(
            new ProgressWidget().value(
                new DoubleSyncValue(() -> (double) tile.inventory.getStoredQuantity() / tile.inventory.getCapacity()))
                .texture(STORAGE_PROGRESS, 8)
                .direction(ProgressWidget.Direction.RIGHT)
                .size(32, 4)
                .leftRel(1f)
                .anchorLeft(1f)
                .marginTop(10)
                .marginRight(7)
                .tooltipAutoUpdate(true)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "gui.cabinet.storage",
                            formatNumber(
                                sync.stored()
                                    .getIntValue()),
                            formatNumber(
                                sync.capacity()
                                    .getIntValue())))));
    }

    private IWidget buildTitle() {
        return new ParentWidget<>().coverChildren()
            .leftRel(0f)
            .anchorLeft(0f)
            .marginTop(6)
            .marginLeft(7)
            .child(
                IKey.str(StatCollector.translateToLocal(tile.getInventoryName()))
                    .asWidget());
    }

    private <E extends Enum<E>> EnumSyncValue<E, ?> syncEnum(Class<E> enumClass, Supplier<E> supplier,
        Consumer<E> consumer, Runnable refresh) {
        EnumSyncValue<E, ?> value = new EnumSyncValue<>(enumClass, supplier, v -> {
            consumer.accept(v);
            refresh.run();
            tile.markDirty();
        });
        value.allowC2S(true);
        return value;
    }

    private static String settingTip(String titleKey, String valueKey) {
        return StatCollector.translateToLocal(titleKey) + ": " + StatCollector.translateToLocal(valueKey);
    }

    private void createSettingButtons(Runnable triggerRefresh) {
        var sortSync = syncEnum(SortType.class, () -> tile.sortType, v -> tile.sortType = v, triggerRefresh);

        var scrollSync = syncEnum(
            ScrollDirection.class,
            () -> tile.scrollDirection,
            v -> tile.scrollDirection = v,
            triggerRefresh);

        var slotSync = syncEnum(
            SlotDirection.class,
            () -> tile.slotDirection,
            v -> tile.slotDirection = v,
            triggerRefresh);

        sortButton = new CycleButtonWidget().value(sortSync)
            .stateOverlay(SortType.BY_NAME, GuiTextures.ARROW_DOWN)
            .stateOverlay(SortType.BY_MOD_ID, GuiTextures.BOOKMARK)
            .stateOverlay(SortType.BY_COUNT, GuiTextures.GRAPH)
            .stateOverlay(SortType.BY_ORE_DICT, GuiTextures.MATERIAL)
            .tooltipBuilder(
                SortType.BY_NAME.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.sort.title", "gui.cabinet.sort.name")))
            .tooltipBuilder(
                SortType.BY_MOD_ID.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.sort.title", "gui.cabinet.sort.mod_id")))
            .tooltipBuilder(
                SortType.BY_COUNT.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.sort.title", "gui.cabinet.sort.count")))
            .tooltipBuilder(
                SortType.BY_ORE_DICT.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.sort.title", "gui.cabinet.sort.ore_dict")))
            .leftRel(0.5f)
            .anchorLeft(0.5f)
            .size(12);

        scrollButton = new CycleButtonWidget().value(scrollSync)
            .stateOverlay(ScrollDirection.HORIZONTAL, GuiTextures.MOVE_RIGHT)
            .stateOverlay(ScrollDirection.VERTICAL, GuiTextures.MOVE_DOWN)
            .tooltipBuilder(
                ScrollDirection.HORIZONTAL.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.scroll.title", "gui.cabinet.scroll.horizontal")))
            .tooltipBuilder(
                ScrollDirection.VERTICAL.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.scroll.title", "gui.cabinet.scroll.vertical")))
            .leftRel(0.5f)
            .anchorLeft(0.5f)
            .topRel(0.5f)
            .anchorTop(0.5f)
            .size(12);

        slotDirButton = new CycleButtonWidget().value(slotSync)
            .stateOverlay(SlotDirection.HORIZONTAL, GuiTextures.RIGHTLOAD)
            .stateOverlay(SlotDirection.VERTICAL, GuiTextures.DOWNLOAD)
            .tooltipBuilder(
                SlotDirection.HORIZONTAL.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.slot_dir.title", "gui.cabinet.slot_dir.horizontal")))
            .tooltipBuilder(
                SlotDirection.VERTICAL.ordinal(),
                tt -> tt.addLine(settingTip("gui.cabinet.slot_dir.title", "gui.cabinet.slot_dir.vertical")))
            .leftRel(0.5f)
            .anchorLeft(0.5f)
            .topRel(1f)
            .anchorTop(1f)
            .size(12);
    }

    @Desugar
    private record CabinetSyncValues(IntSyncValue capacity, IntSyncValue stored, IntSyncValue slots,
        IntSyncValue slotsUsed) {}
}
