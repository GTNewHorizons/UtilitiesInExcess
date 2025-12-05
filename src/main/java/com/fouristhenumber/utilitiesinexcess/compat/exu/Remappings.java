package com.fouristhenumber.utilitiesinexcess.compat.exu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.CompressedBlocksTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.DarkPortalTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.DecoBlock1Transformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.SoundMufflerTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.TrashCanTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.DivisionSigilTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.GoldenBagTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.GoldenLassoTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.UnstableIngotTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items.WateringCanTransformation;

public enum Remappings {
    // spotless:off

    // make sure to leave a trailing comma
    // Direct block remappings
    PURE_LOVE("pureLove", ModBlocks.PURE_LOVE),
    // Direct item remappings

    // Item Transformations
    GOLDEN_BAG(new GoldenBagTransformation()),
    WATERING_CAN(new WateringCanTransformation()),
    INVERSION_SIGIL(new DivisionSigilTransformation()),
    MOB_JAR(new GoldenLassoTransformation()),
    INVERTED_INGOT(new UnstableIngotTransformation()),
    // Block Transformations
    SOUND_MUFFLER(new SoundMufflerTransformation()),
    DECO_BLOCK_1(new DecoBlock1Transformation()),
    COMPRESSED_BLOCKS(new CompressedBlocksTransformation()),
    DARK_PORTAL(new DarkPortalTransformation()),
    TRASH_CANS(new TrashCanTransformation()),

    ; // leave trailing semicolon
    // spotless:on

    public static final Remappings[] VALUES = values();

    public static final HashMap<String, Item> itemMappings = new HashMap<>();
    public static final HashMap<String, Block> blockMappings = new HashMap<>();
    public static final List<IPosteaTransformation> transformations = new ArrayList<>();

    public static void init() {
        for (Remappings remapping : VALUES) {
            if (remapping.replacementItem != null) {
                itemMappings.put(remapping.getName(), remapping.replacementItem);
            }
            if (remapping.replacementBlock != null) {
                blockMappings.put(remapping.getName(), remapping.replacementBlock);
            }
            if (remapping.transformation != null) {
                transformations.add(remapping.transformation);
            }
        }
    }

    public static void initTransformationMappings() {
        for (Remappings remapping : VALUES) {
            if (remapping.transformation != null) {
                remapping.transformation.addItemRemappings(itemMappings);
                remapping.transformation.addBlockRemappings(blockMappings);
            }
        }
    }

    private String oldName;
    private Block replacementBlock = null;
    private Item replacementItem = null;
    private IPosteaTransformation transformation = null;

    private static final String pre = "ExtraUtilities:";

    Remappings(String oldName, ModBlocks modBlock) {
        this.oldName = pre + oldName;
        this.replacementBlock = modBlock.get();
        this.replacementItem = modBlock.getItem();
    }

    Remappings(String oldName, ModItems modItem) {
        this.oldName = pre + oldName;
        this.replacementItem = modItem.get();
    }

    Remappings(IPosteaTransformation posteaTransformation) {
        this.transformation = posteaTransformation;
    }

    public String getName() {
        return oldName;
    }
}
