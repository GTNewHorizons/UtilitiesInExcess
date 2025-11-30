package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import static net.minecraft.util.EnumChatFormatting.DARK_GRAY;
import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;
import static net.minecraft.util.EnumChatFormatting.GOLD;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.tools.TinkerTools;

public class TinkersMaterials {

    public static int invertedID = 5657;
    public static int bedrockiumID = 5658;
    public static int magicalWoodID = 5659;

    public static void registerMaterials() {
        // TConstructClientRegistry.addMaterialRenderMapping(MaterialID.NagaScale, "tinker", "nagascale", true);

        if (InversionConfig.enableInvertedIngot) {
            TConstructRegistry
                .addToolMaterial(invertedID, "inverted", 4, 100, 700, 2, 0.6F, 4, 0, DARK_GREEN.toString(), 0x53763B);

            TConstructRegistry.addBowMaterial(invertedID, 109, 1f);
            TConstructRegistry.addArrowMaterial(invertedID, 2.4F, 0F);

            doGenericRegistration(
                invertedID,
                "inverted",
                new ItemStack(ModItems.INVERTED_INGOT.get(), 1, OreDictionary.WILDCARD_VALUE));
        }
        if (ItemConfig.enableBedrockium) {
            TConstructRegistry.addToolMaterial(
                bedrockiumID,
                "bedrockium_uie",
                7,
                7500,
                800,
                4,
                1.75F,
                0,
                0,
                DARK_GRAY.toString(),
                0xFFFFFF);

            TConstructRegistry.addBowMaterial(bedrockiumID, 200, 3f);
            TConstructRegistry.addArrowMaterial(bedrockiumID, 40F, 0.4F);

            doGenericRegistration(bedrockiumID, "bedrockium_uie", new ItemStack(ModItems.BEDROCKIUM_INGOT.get(), 1, 0));
        }
        if (BlockConfig.enableMagicWood) {
            TConstructRegistry
                .addToolMaterial(magicalWoodID, "magical_wood", 2, 97, 150, 0, 1F, 0, 0, GOLD.toString(), 0x734829);

            TConstructRegistry.addBowMaterial(magicalWoodID, 18, 3f);
            TConstructRegistry.addArrowMaterial(magicalWoodID, 0.69F, 0.5F);

            // Makes part builder recipes for magical wood
            for (int meta = 0; meta < TinkerTools.patternOutputs.length; meta++) {
                if (TinkerTools.patternOutputs[meta] != null) {
                    TConstructRegistry.addPartMapping(
                        TinkerTools.woodPattern,
                        meta + 1,
                        magicalWoodID,
                        new ItemStack(TinkerTools.patternOutputs[meta], 1, magicalWoodID));
                }
            }

            doGenericRegistration(magicalWoodID, "magical_wood", new ItemStack(ModBlocks.MAGIC_WOOD.getItem(), 1, 0));
        }
    }

    private static void doGenericRegistration(int id, String name, ItemStack item) {
        TConstructRegistry.toolMaterialStrings
            .put(StatCollector.translateToLocal("material." + name), TConstructRegistry.toolMaterials.get(id));

        PatternBuilder.instance.registerFullMaterial(
            item,
            2,
            name,
            new ItemStack(TinkerTools.toolShard, 1, id),
            new ItemStack(TinkerTools.toolRod, 1, id),
            id);

        TConstructRegistry.addDefaultToolPartMaterial(id);
        TConstructRegistry.addDefaultShardMaterial(id);
    }
}
