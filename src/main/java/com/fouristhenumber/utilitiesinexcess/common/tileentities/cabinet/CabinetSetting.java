package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet;

import java.util.Comparator;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.widget.scroll.HorizontalScrollData;
import com.cleanroommc.modularui.widget.scroll.ScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;

import cpw.mods.fml.common.registry.GameData;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;

public class CabinetSetting {

    public enum SortType {

        BY_NAME {

            @Override
            public Comparator<ItemStack> baseComparator() {
                return Comparator
                    .comparing(SortType::getDisplayNameSafe, Comparator.nullsLast(Comparator.naturalOrder()));
            }
        },

        BY_MOD_ID {

            @Override
            public Comparator<ItemStack> baseComparator() {
                return Comparator.comparing(SortType::getModIdSafe, Comparator.nullsLast(Comparator.naturalOrder()));
            }
        },

        BY_COUNT {

            @Override
            public Comparator<ItemStack> baseComparator() {
                return Comparator
                    .comparing(s -> (s == null ? null : s.stackSize), Comparator.nullsLast(Comparator.reverseOrder()));
            }
        },

        BY_ORE_DICT {

            @Override
            public Comparator<ItemStack> baseComparator() {
                return Comparator.comparing(SortType::getOreDictSafe, Comparator.nullsLast(Comparator.naturalOrder()));
            }
        };

        public Comparator<ItemStack> stackComparator() {
            return baseComparator()
                .thenComparing(SortType::getModIdSafe, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(s -> (s == null ? 0 : s.stackSize))
                .thenComparing(SortType::getOreDictSafe, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(SortType::getDisplayNameSafe, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        public abstract Comparator<ItemStack> baseComparator();

        private static String getDisplayNameSafe(ItemStack stack) {
            if (stack == null || stack.getItem() == null) return null;
            return stack.getDisplayName();
        }

        private static String getModIdSafe(ItemStack stack) {
            if (stack == null || stack.getItem() == null) return null;
            String id = GameData.getItemRegistry()
                .getNameForObject(stack.getItem());
            if (id == null) return "unknown";
            int i = id.indexOf(":");
            return i == -1 ? "minecraft" : id.substring(0, i);
        }

        private static String getOreDictSafe(ItemStack stack) {
            if (stack == null || stack.getItem() == null) return null;
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids.length == 0) return "";
            return OreDictionary.getOreName(ids[0]);
        }
    }

    public enum ScrollDirection {

        HORIZONTAL(slots -> MathHelper.ceiling_double_int(slots / 5.0D), new HorizontalScrollData(false, 10)), // max 5
                                                                                                               // rows
        VERTICAL(slots -> 8, new VerticalScrollData(false, 10)); // max 9 columns

        private final IntUnaryOperator rowSizeCalculator;
        private final ScrollData scrollData;

        ScrollDirection(IntUnaryOperator rowSizeCalculator, ScrollData scrollData) {
            this.rowSizeCalculator = rowSizeCalculator;
            this.scrollData = scrollData;
        }

        public boolean isHorizontal() {
            return this == HORIZONTAL;
        }

        public int getRowSize(int slots) {
            return rowSizeCalculator.apply(slots);
        }

        public ScrollData getScrollData() {
            return scrollData;
        }
    }

    public enum SlotDirection {

        HORIZONTAL,
        VERTICAL;

        public boolean isHorizontal() {
            return this == HORIZONTAL;
        }
    }
}
