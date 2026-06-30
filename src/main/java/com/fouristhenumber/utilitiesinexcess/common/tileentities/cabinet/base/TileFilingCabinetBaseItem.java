package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.base;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Interpolation;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.Expandable;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.CabinetSetting;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.fouristhenumber.utilitiesinexcess.inventory.CabinetInventory;
import com.fouristhenumber.utilitiesinexcess.utils.NbtHelper;

public abstract class TileFilingCabinetBaseItem extends TileFilingCabinetBase implements ISidedInventory {

    public CabinetInventory inventory;

    public CabinetSetting.SortType sortType = CabinetSetting.SortType.BY_NAME;
    public CabinetSetting.ScrollDirection scrollDirection = CabinetSetting.ScrollDirection.VERTICAL;
    public CabinetSetting.SlotDirection slotDirection = CabinetSetting.SlotDirection.HORIZONTAL;

    private static final String NBT_SORT_TYPE = "SortType";
    private static final String NBT_SCROLL_DIR = "ScrollDir";
    private static final String NBT_SLOT_DIR = "SlotDir";

    private CycleButtonWidget SortButton;
    private CycleButtonWidget ScrollButton;
    private CycleButtonWidget SlotDirButton;

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

    public abstract boolean isItemAllowed(ItemStack stack);

    public TileFilingCabinetBaseItem(FilingCabinetsConfig.CabinetConfig config, Predicate<ItemStack> upgradeMatcher) {
        super(upgradeMatcher);
        this.inventory = new CabinetInventory(
            this,
            config.numSlots,
            config.numItems,
            FilingCabinetsConfig.upgradeCapacity);

    }

    @Override
    public ModularPanel buildUICabinet(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        IntSyncValue capacity = new IntSyncValue(() -> inventory.getCapacity());
        IntSyncValue stored = new IntSyncValue(() -> inventory.getStoredQuantity());
        IntSyncValue slots = new IntSyncValue(() -> inventory.getSlots());
        IntSyncValue slotsUsed = new IntSyncValue(() -> inventory.getSlotsUsed());

        syncManager.syncValue("capacity", capacity);
        syncManager.syncValue("stored", stored);
        syncManager.syncValue("slots", slots);
        syncManager.syncValue("slotsUsed", slotsUsed);

        DynamicSyncHandler gridRefresher = new DynamicSyncHandler();

        Runnable triggerRefresh = () -> {
            gridRefresher.notifyUpdate(buf -> {
                buf.writeInt(this.sortType.ordinal());
                buf.writeInt(this.scrollDirection.ordinal());
                buf.writeInt(this.slotDirection.ordinal());
            });
        };

        List<ItemSlot> itemSlotList = new ArrayList<>();
        SlotGroup connected = new SlotGroup(
            "cabinet_inventory",
            scrollDirection.getRowSize(this.inventory.getSlots()),
            true);

        syncManager.registerSlotGroup(connected);

        for (int i = 0; i < this.inventory.getSlots(); i++) {
            int finalI = i;
            ItemSlot slotWidget = new ItemSlot().syncHandler(
                syncManager.getOrCreateSyncHandler(
                    "slot_" + i,
                    i,
                    ItemSlotSH.class,
                    () -> new ItemSlotSH(
                        new ModularSlot(this.inventory, finalI).slotGroup(connected)
                            .ignoreMaxStackSize(true)
                            .changeListener((newItem, onlyAmountChanged, client, init) -> {
                                inventory.updateCounts();
                                triggerRefresh.run();
                            }))));
            itemSlotList.add(slotWidget);
        }

        gridRefresher.widgetProvider((sm, buf) -> {
            int sortOrd = buf.readInt();
            int scrollOrd = buf.readInt();
            int slotOrd = buf.readInt();

            CabinetSetting.SortType sort = CabinetSetting.SortType.values()[sortOrd];
            CabinetSetting.ScrollDirection scroll = CabinetSetting.ScrollDirection.values()[scrollOrd];
            CabinetSetting.SlotDirection slotDir = CabinetSetting.SlotDirection.values()[slotOrd];

            itemSlotList.sort((slotA, slotB) -> {
                ItemStack stackA = slotA.getSlot()
                    .getStack();
                ItemStack stackB = slotB.getSlot()
                    .getStack();
                return sort.stackComparator()
                    .compare(stackA, stackB);
            });

            int slotSize = 18;
            int cols = 9;
            int rows = 5;

            int rowSize = scroll.getRowSize(this.inventory.getSlots());
            int slotsRow = slotSize * cols + (!scroll.isHorizontal() ? -8 : 0);
            int slotsCol = slotSize * rows + (scroll.isHorizontal() ? 10 : 0);

            Grid inventoryGrid = new Grid().name("Grid")
                .scrollable(scroll.getScrollData())
                .width(slotsRow)
                .height(slotsCol)
                .matrix(safeMatrix(rowSize, itemSlotList, slotDir.isHorizontal()));

            return inventoryGrid;
        });

        DynamicSyncedWidget<?> dynamicGridWidget = new DynamicSyncedWidget<>();
        dynamicGridWidget.syncHandler(gridRefresher);
        dynamicGridWidget.relativeToParent()
            .align(Alignment.TopCenter)
            .marginTop(20)
            .coverChildren();

        ModularPanel panel = new ModularPanel("Cabinet");
        panel.size(176, 217);
        panel.bindPlayerInventory();

        panel.child(dynamicGridWidget);

        setCabinetSettingButtons(triggerRefresh);

        // todo have a proper icon
        Expandable expandable = new Expandable().name("expandable")
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
                new ItemDrawable(Blocks.crafting_table).asIcon()
                    .asWidget()
                    .size(20)
                    .pos(0, 0))
            .expandedView(
                new ParentWidget<>().name("crafting tab")
                    .child(
                        new ItemDrawable(Blocks.crafting_table).asIcon()
                            .asWidget()
                            .size(20)
                            .pos(0, -20))
                    .child(SortButton)
                    .child(ScrollButton)
                    .child(SlotDirButton)
                    .size(20, 54)
                    .margin(0, 0, 20, 5));

        panel.child(expandable);

        panel.child(
            new ProgressWidget()
                .value(new DoubleSyncValue(() -> (double) inventory.getSlotsUsed() / inventory.getSlots()))
                .texture(SLOT_PROGRESS, 4)
                .direction(ProgressWidget.Direction.RIGHT)
                .size(32, 4)
                .align(Alignment.TopRight)
                .marginTop(6)
                .marginRight(7)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "gui.cabinet.slot",
                            formatNumber(slotsUsed.getIntValue()),
                            formatNumber(slots.getIntValue())))));

        panel.child(
            new ProgressWidget()
                .value(new DoubleSyncValue(() -> (double) inventory.getStoredQuantity() / inventory.getCapacity()))
                .texture(STORAGE_PROGRESS, 8)
                .direction(ProgressWidget.Direction.RIGHT)
                .size(32, 4)
                .align(Alignment.TopRight)
                .marginTop(10)
                .marginRight(7)
                .tooltipDynamic(
                    tt -> tt.add(
                        StatCollector.translateToLocalFormatted(
                            "gui.cabinet.storage",
                            formatNumber(stored.getIntValue()),
                            formatNumber(capacity.getIntValue())))));

        panel.child(
            new ParentWidget<>().coverChildren()
                .align(Alignment.TopLeft)
                .marginTop(6)
                .marginLeft(7)
                .child(
                    IKey.str(StatCollector.translateToLocal(getInventoryName()))
                        .asWidget()));

        triggerRefresh.run();
        return panel;
    }

    private <E extends Enum<E>> EnumSyncValue<E> syncEnum(Class<E> Enum, Supplier<E> supplier, Consumer<E> consumer,
        Runnable refresh) {
        return new EnumSyncValue<>(Enum, supplier, value -> {
            consumer.accept(value);
            refresh.run();
            markDirty();
        });
    }

    public void setCabinetSettingButtons(Runnable triggerRefresh) {
        var sortSync = syncEnum(CabinetSetting.SortType.class, () -> sortType, v -> sortType = v, triggerRefresh);

        var scrollSync = syncEnum(
            CabinetSetting.ScrollDirection.class,
            () -> scrollDirection,
            v -> scrollDirection = v,
            triggerRefresh);

        var slotSync = syncEnum(
            CabinetSetting.SlotDirection.class,
            () -> slotDirection,
            v -> slotDirection = v,
            triggerRefresh);

        // todo change the rich tooltip to use lang keys
        SortButton = new CycleButtonWidget().value(sortSync)
            .stateOverlay(CabinetSetting.SortType.BY_NAME, GuiTextures.ARROW_DOWN)
            .stateOverlay(CabinetSetting.SortType.BY_MOD_ID, GuiTextures.ARROW_UP)
            .stateOverlay(CabinetSetting.SortType.BY_COUNT, GuiTextures.ALL_DIRECTIONS)
            .stateOverlay(CabinetSetting.SortType.BY_ORE_DICT, GuiTextures.BOOKMARK)
            .tooltipBuilder(CabinetSetting.SortType.BY_NAME.ordinal(), tt -> tt.addLine("Sort: Name"))
            .tooltipBuilder(CabinetSetting.SortType.BY_MOD_ID.ordinal(), tt -> tt.addLine("Sort: Mod ID"))
            .tooltipBuilder(CabinetSetting.SortType.BY_COUNT.ordinal(), tt -> tt.addLine("Sort: Count"))
            .tooltipBuilder(CabinetSetting.SortType.BY_ORE_DICT.ordinal(), tt -> tt.addLine("Sort: Ore Dictionary"))
            .align(Alignment.TopCenter)
            .size(12);

        ScrollButton = new CycleButtonWidget().value(scrollSync)
            .stateOverlay(CabinetSetting.ScrollDirection.HORIZONTAL, GuiTextures.CHECK_BOX)
            .stateOverlay(CabinetSetting.ScrollDirection.VERTICAL, GuiTextures.CHECK_BOX_FULL)
            .tooltipBuilder(CabinetSetting.ScrollDirection.HORIZONTAL.ordinal(), tt -> tt.addLine("Sort: Name"))
            .tooltipBuilder(CabinetSetting.ScrollDirection.VERTICAL.ordinal(), tt -> tt.addLine("Sort: Mod ID"))
            .align(Alignment.Center)
            .size(12);

        SlotDirButton = new CycleButtonWidget().value(slotSync)
            .stateOverlay(CabinetSetting.SlotDirection.HORIZONTAL, GuiTextures.PROCESSOR)
            .stateOverlay(CabinetSetting.SlotDirection.VERTICAL, GuiTextures.PASTE)
            .tooltipBuilder(CabinetSetting.SlotDirection.HORIZONTAL.ordinal(), tt -> tt.addLine("Sort: Name"))
            .tooltipBuilder(CabinetSetting.SlotDirection.VERTICAL.ordinal(), tt -> tt.addLine("Sort: Mod ID"))
            .align(Alignment.BottomCenter)
            .size(12);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound inventoryTag = this.inventory.serializeNBT();
        compound.setTag("Inventory", inventoryTag);

        NbtHelper.writeEnumToNBT(compound, NBT_SORT_TYPE, sortType);
        NbtHelper.writeEnumToNBT(compound, NBT_SCROLL_DIR, scrollDirection);
        NbtHelper.writeEnumToNBT(compound, NBT_SLOT_DIR, slotDirection);

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
            this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        }

        sortType = NbtHelper.readEnumFromNBT(
            compound,
            NBT_SORT_TYPE,
            CabinetSetting.SortType.values(),
            CabinetSetting.SortType.BY_NAME);
        scrollDirection = NbtHelper.readEnumFromNBT(
            compound,
            NBT_SCROLL_DIR,
            CabinetSetting.ScrollDirection.values(),
            CabinetSetting.ScrollDirection.VERTICAL);
        slotDirection = NbtHelper.readEnumFromNBT(
            compound,
            NBT_SLOT_DIR,
            CabinetSetting.SlotDirection.values(),
            CabinetSetting.SlotDirection.HORIZONTAL);

    }

    @Override
    public int getSizeInventory() {
        return inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return inventory.extractItem(slot, count, false);
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getRemainingCapacity();
    }

    @Override
    public String getInventoryName() {
        return "tile.filing_cabinet." + getCabinetType().getName() + ".name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.inventory.isItemValid(slot, stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] slots = new int[getSizeInventory()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return this.inventory.getStackInSlot(slot) != null;
    }

    public static <I extends IWidget> List<List<I>> safeMatrix(int rowLength, List<I> list, boolean slotsHorizontal) {
        List<List<I>> matrix = new ArrayList<>();

        if (slotsHorizontal) {
            matrix = Grid.mapToMatrix(rowLength, list.size(), list::get);
        } else {
            int numCols = rowLength;
            int numRows = (int) Math.ceil((double) list.size() / numCols);

            for (int r = 0; r < numRows; r++) {
                matrix.add(new ArrayList<>());
            }

            for (int i = 0; i < list.size(); i++) {
                int row = i % numRows;
                matrix.get(row)
                    .add(list.get(i));
            }
        }

        int maxCols = 0;
        for (List<I> row : matrix) {
            maxCols = Math.max(maxCols, row.size());
        }
        for (List<I> row : matrix) {
            while (row.size() < maxCols) {
                row.add(null);
            }
        }

        return matrix;
    }

}
