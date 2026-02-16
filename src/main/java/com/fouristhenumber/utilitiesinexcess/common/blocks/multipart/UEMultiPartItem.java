package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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

    // TODO allow placing on multiple sides.
    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        int materialID = getMaterialID(item);
        int damage = item.getItemDamage();
        if (materialID < 0 || damage > partNames.length)
        {
            return false;
        }

        MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
        BlockCoord position = new BlockCoord(x, y, z).offset(side);
        TMultiPart potentialPart = new Content().createUEMultiPart(false, materialID, ForgeDirection.OPPOSITES[side], partNames[damage]);
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && TileMultipart.canPlacePart(world, position, potentialPart)) {
            if (!world.isRemote) {
                TileMultipart.addPart(world, position, potentialPart);
                if (!player.capabilities.isCreativeMode)
                    item.stackSize--;

                Block.SoundType sound = MicroMaterialRegistry.getMaterial(materialID).getSound();
                if (sound != null)
                    world.playSoundEffect(
                        x + 0.5d,
                        y + 0.5d,
                        z + 0.5d,
                        sound.func_150496_b(),
                        (sound.getVolume() + 1.0f) / 2.0f,
                        sound.getPitch() * 0.8f
                    );
            }

            return true;
        }

        return false;
    }

    public static ItemStack createStack(String material, int damage)
    {
        ItemStack stack = new ItemStack(ModItems.UE_MULTI_PART.get(), 1, damage);
        stack.stackTagCompound = new NBTTagCompound();
        stack.getTagCompound().setString("mat", material);
        return stack;
    }

    public static ItemStack createStack(UEMultiPart part)
    {
        if (part instanceof MaterialBasedPart matPart)
        {
            return createStack(matPart.material, Content.partMap.get(part.getType()));
        }
        return null;
    }

    public static ItemStack createStack(int materialID, int damage)
    {
        return createStack(MicroMaterialRegistry.materialName(materialID), damage);
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

    public static int getMaterialID(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            if (!stack.getTagCompound().hasKey("mat"))
            {
                return 0;
            }
            return MicroMaterialRegistry.materialID(stack.getTagCompound().getString("mat"));
        }
        return 0;
    }
}
