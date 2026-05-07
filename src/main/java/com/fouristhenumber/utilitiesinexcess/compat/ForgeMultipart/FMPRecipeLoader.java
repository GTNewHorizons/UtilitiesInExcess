package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.recipe.DisableableItemStack;

import codechicken.microblock.EdgeMicroClass;
import codechicken.microblock.FaceMicroClass;
import codechicken.microblock.ItemMicroPart;
import codechicken.microblock.MicroMaterialRegistry;
import scala.Tuple2;

public class FMPRecipeLoader {

    public static void run() {

        for (Tuple2<String, MicroMaterialRegistry.IMicroMaterial> material : MicroMaterialRegistry.getIdMap()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("mat", material._1());

            // Fence
            addShapedRecipe(
                withTag(FMPItems.UE_MULTI_PART.newItemStack(2, 0), tag),
                "pPp",
                "pPp",
                " P ",
                'p',
                ItemMicroPart.create((EdgeMicroClass.getClassId() << 8) | 2, material._1()), // Post
                'P',
                ItemMicroPart.create((EdgeMicroClass.getClassId() << 8) | 4, material._1()) // Pillar
            );

            // Wall
            addShapedRecipe(
                withTag(FMPItems.UE_MULTI_PART.newItemStack(5, 1), tag),
                " B ",
                "SBS",
                "SBS",
                'B',
                MicroMaterialRegistry.getMaterial(material._1())
                    .getItem(), // Block the material is derived from
                'S',
                ItemMicroPart.create((FaceMicroClass.getClassId() << 8) | 4, material._1()) // Slab
            );

            // Sphere
            addShapedRecipe(
                withTag(FMPItems.UE_MULTI_PART.newItemStack(1, 2), tag),
                "CSC",
                "SUS",
                "CSC",
                'S',
                ItemMicroPart.create((FaceMicroClass.getClassId() << 8) | 4, material._1()), // Slab
                'B',
                ModBlocks.INVERTED_BLOCK.newItemStack(),
                'C',
                ItemMicroPart.create((FaceMicroClass.getClassId() << 8) | 1, material._1()) // Cover
            );
        }
    }

    public static ItemStack withTag(ItemStack stack, NBTTagCompound tag) {
        stack.setTagCompound(tag);
        return stack;
    }

    private static boolean addShapedRecipe(Object outputObject, Object... params) {
        return DisableableItemStack.addShapedRecipe(outputObject, params);
    }
}
