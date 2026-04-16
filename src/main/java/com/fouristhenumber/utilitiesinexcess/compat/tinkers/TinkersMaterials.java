package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import static net.minecraft.util.EnumChatFormatting.DARK_GRAY;
import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static tconstruct.TConstruct.basinCasting;
import static tconstruct.TConstruct.tableCasting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;

public class TinkersMaterials {

    public static Fluid invertedFluid = TinkerSmeltery.registerFluid("inverted");
    public static Fluid bedrockiumFluid = TinkerSmeltery.registerFluid("bedrockium");

    public static void registerMaterials() {

        int invertedID = OtherConfig.invertedTinkersID;
        int bedrockiumID = OtherConfig.bedrockiumTinkersID;
        int magicalWoodID = OtherConfig.magicalWoodTinkersID;

        if (InversionConfig.enableInvertedIngot) {
            TConstructRegistry
                .addToolMaterial(invertedID, "inverted", 4, 100, 700, 2, 0.6F, 4, 0, DARK_GREEN.toString(), 0x53763B);

            TConstructRegistry.addBowMaterial(invertedID, 109, 1f);
            TConstructRegistry.addArrowMaterial(invertedID, 2.4F, 0F);

            doGenericRegistration(
                invertedID,
                "inverted",
                new ItemStack(ModItems.INVERTED_INGOT.get(), 1, OreDictionary.WILDCARD_VALUE));

            FluidRegistry.registerFluid(invertedFluid);
            FluidType.registerFluidType(
                invertedFluid.getName(),
                ModBlocks.INVERTED_BLOCK.get(),
                0,
                850,
                invertedFluid,
                true);
            Smeltery.addMelting(
                ModBlocks.INVERTED_BLOCK.newItemStack(),
                ModBlocks.INVERTED_BLOCK.get(),
                0,
                850,
                new FluidStack(invertedFluid, TConstruct.blockLiquidValue));
            Smeltery.addMelting(
                ModItems.INVERTED_INGOT.newItemStack(),
                ModBlocks.INVERTED_BLOCK.get(),
                0,
                850,
                new FluidStack(invertedFluid, TConstruct.ingotLiquidValue));
            Smeltery.addMelting(
                ModItems.INVERTED_INGOT.newItemStack(1, 1),
                ModBlocks.INVERTED_BLOCK.get(),
                0,
                850,
                new FluidStack(invertedFluid, TConstruct.ingotLiquidValue));
            Smeltery.addMelting(
                ModItems.INVERTED_NUGGET.newItemStack(),
                ModBlocks.INVERTED_BLOCK.get(),
                0,
                850,
                new FluidStack(invertedFluid, TConstruct.nuggetLiquidValue));

            basinCasting.addCastingRecipe(
                ModBlocks.INVERTED_BLOCK.newItemStack(),
                new FluidStack(invertedFluid, TConstruct.blockLiquidValue),
                100);

            NBTTagCompound smelteryTag = new NBTTagCompound();
            smelteryTag.setInteger("MaterialId", invertedID);
            smelteryTag.setString("FluidName", invertedFluid.getName());
            FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", smelteryTag);
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

            doGenericRegistration(bedrockiumID, "bedrockium_uie", new ItemStack(ModItems.BEDROCKIUM_INGOT.get()));

            FluidRegistry.registerFluid(bedrockiumFluid);
            FluidType.registerFluidType(
                bedrockiumFluid.getName(),
                ModBlocks.BEDROCKIUM_BLOCK.get(),
                0,
                850,
                bedrockiumFluid,
                true);
            Smeltery.addMelting(
                ModBlocks.BEDROCKIUM_BLOCK.newItemStack(),
                ModBlocks.BEDROCKIUM_BLOCK.get(),
                0,
                850,
                new FluidStack(bedrockiumFluid, TConstruct.blockLiquidValue));
            Smeltery.addMelting(
                ModItems.BEDROCKIUM_INGOT.newItemStack(),
                ModBlocks.BEDROCKIUM_BLOCK.get(),
                0,
                850,
                new FluidStack(bedrockiumFluid, TConstruct.ingotLiquidValue));

            tableCasting.addCastingRecipe(
                ModItems.BEDROCKIUM_INGOT.newItemStack(),
                new FluidStack(bedrockiumFluid, TConstruct.ingotLiquidValue),
                new ItemStack(TinkerSmeltery.metalPattern, 1, 0),
                false,
                50);
            if (GameRegistry.findItem("TConstruct", "clayPattern") != null) {
                tableCasting.addCastingRecipe(
                    ModItems.BEDROCKIUM_INGOT.newItemStack(),
                    new FluidStack(bedrockiumFluid, TConstruct.ingotLiquidValue),
                    new ItemStack(TinkerSmeltery.clayPattern, 1, 0),
                    true,
                    50);
            }
            basinCasting.addCastingRecipe(
                ModBlocks.BEDROCKIUM_BLOCK.newItemStack(),
                new FluidStack(bedrockiumFluid, TConstruct.blockLiquidValue),
                100);

            NBTTagCompound smelteryTag = new NBTTagCompound();
            smelteryTag.setInteger("MaterialId", bedrockiumID);
            smelteryTag.setString("FluidName", bedrockiumFluid.getName());
            FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", smelteryTag);
        }
        if (BlockConfig.enableMagicWood) {
            TConstructRegistry
                .addToolMaterial(magicalWoodID, "magical_wood", 2, 97, 150, 0, 1F, 0, 0, GOLD.toString(), 0x734829);

            TConstructRegistry.addBowMaterial(magicalWoodID, 18, 3f);
            TConstructRegistry.addArrowMaterial(magicalWoodID, 0.69F, 0.5F);

            NBTTagCompound woodTag = new NBTTagCompound();
            ModBlocks.MAGIC_WOOD.newItemStack()
                .writeToNBT(woodTag);

            NBTTagCompound partTag = new NBTTagCompound();
            partTag.setTag("Item", woodTag);
            partTag.setInteger("MaterialId", magicalWoodID);
            partTag.setInteger("Value", 2);
            FMLInterModComms.sendMessage("TConstruct", "addPartBuilderMaterial", partTag);

            doGenericRegistration(magicalWoodID, "magical_wood", new ItemStack(ModBlocks.MAGIC_WOOD.getItem()));

        }
    }

    private static void doGenericRegistration(int id, String name, ItemStack item) {
        TConstructClientRegistry.addMaterialRenderMapping(id, "tinker", name, true);

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
