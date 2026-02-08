package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.microblock.MicroMaterialRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.Arrays;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.multipart.Content.partNames;

// Most of this is just ripped from FMP ngl
public class UEMultiPartItem extends Item {


    public UEMultiPartItem() {
        setUnlocalizedName("ue_microPartItem");
        setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        MicroMaterialRegistry.IMicroMaterial material = getMaterial(stack);
        int damage = getDamage(stack);
        if (material == null || damage > partNames.length)
        {
            return "Unnamed";
        }
        return StatCollector.translateToLocalFormatted(
            partNames[0] + ".name",
            material.getLocalizedName());
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> listOfStacks)
    {
        Arrays.stream(MicroMaterialRegistry.getIdMap()).forEach(
            t -> {
                for (int i = 0; i < partNames.length; i++) {
                    listOfStacks.add(createStack(t._1, i));
                }
            }
        );
    }

    private ItemStack createStack(String material, int damage)
    {
        ItemStack stack = new ItemStack(this, 1, damage);
        stack.stackTagCompound = new NBTTagCompound();
        stack.getTagCompound().setString("mat", material);
        return stack;
    }

    public static MicroMaterialRegistry.IMicroMaterial getMaterial(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            if (!stack.getTagCompound().hasKey("mat"))
            {
                return null;
            }
            return MicroMaterialRegistry.getMaterial(stack.getTagCompound().getString("mat"));
        }
        return null;
    }
}
