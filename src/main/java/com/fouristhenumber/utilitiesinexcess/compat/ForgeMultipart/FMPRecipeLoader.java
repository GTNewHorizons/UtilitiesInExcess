package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.recipe.DisableableItemStack;

import codechicken.microblock.EdgeMicroClass;
import codechicken.microblock.FaceMicroClass;
import codechicken.microblock.ItemMicroPart;
import codechicken.microblock.MicroMaterialRegistry;

public class FMPRecipeLoader {

    public static void run() {

        int materialCount = FMPMaterials.materialCount();
        for (int id = 0; id < materialCount; id++) {
            String materialName = MicroMaterialRegistry.materialName(id);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("mat", materialName);

            // Fence
            addShapedRecipe(
                withTag(FMPItems.UE_MULTI_PART.newItemStack(2, 0), tag),
                "pPp",
                "pPp",
                " P ",
                'p',
                ItemMicroPart.create((EdgeMicroClass.getClassId() << 8) | 2, materialName), // Post
                'P',
                ItemMicroPart.create((EdgeMicroClass.getClassId() << 8) | 4, materialName) // Pillar
            );

            // Wall
            addShapedRecipe(
                withTag(FMPItems.UE_MULTI_PART.newItemStack(5, 1), tag),
                " B ",
                "SBS",
                "SBS",
                'B',
                MicroMaterialRegistry.getMaterial(materialName)
                    .getItem(), // Block the material is derived from
                'S',
                ItemMicroPart.create((FaceMicroClass.getClassId() << 8) | 4, materialName) // Slab
            );

            // Sphere
            addShapedRecipe(
                withTag(FMPItems.UE_MULTI_PART.newItemStack(1, 2), tag),
                "CSC",
                "SUS",
                "CSC",
                'S',
                ItemMicroPart.create((FaceMicroClass.getClassId() << 8) | 4, materialName), // Slab
                'B',
                ModBlocks.INVERTED_BLOCK.newItemStack(),
                'C',
                ItemMicroPart.create((FaceMicroClass.getClassId() << 8) | 1, materialName) // Cover
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
