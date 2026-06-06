package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMobJar extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon emptyIcon;
    @SideOnly(Side.CLIENT)
    private IIcon filledIcon;

    public ItemMobJar() {
        setUnlocalizedName("mob_jar");
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        emptyIcon = register.registerIcon("utilitiesinexcess:mob_jar");
        filledIcon = register.registerIcon("utilitiesinexcess:mob_jar_full");
        itemIcon = emptyIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("MobData")) {
            return filledIcon;
        }
        return emptyIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return getIcon(stack, 0);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey("MobData")) {
            NBTTagCompound mobData = stack.getTagCompound()
                .getCompoundTag("MobData");
            Entity entity = EntityList.createEntityFromNBT(mobData, world);
            if (entity != null) {
                entity.setPosition(player.posX, player.posY, player.posZ);
                world.spawnEntityInWorld(entity);
                stack.getTagCompound()
                    .removeTag("MobData");
            }
        }
        return stack;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        ItemStack currentStack = player.getHeldItem();
        if (!currentStack.hasTagCompound()) {
            currentStack.setTagCompound(new NBTTagCompound());
        }

        if (!currentStack.getTagCompound()
            .hasKey("MobData") && target instanceof EntityLiving && !(target instanceof IMob)) {
            NBTTagCompound entityData = new NBTTagCompound();
            target.writeToNBT(entityData);
            entityData.setString("id", EntityList.getEntityString(target));

            currentStack.getTagCompound()
                .setTag("MobData", entityData);

            if (!target.worldObj.isRemote) {
                target.setDead();
            }

            return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("MobData")) {
            NBTTagCompound mobData = stack.getTagCompound()
                .getCompoundTag("MobData");
            String id = mobData.getString("id");

            // Make a dummy entity solely so that the proper localized name can be retrieved
            Entity dummy = EntityList.createEntityByName(id, player.worldObj);
            if (dummy instanceof EntityLivingBase) {
                String mobName = dummy.getCommandSenderName();
                tooltip.add(StatCollector.translateToLocalFormatted("item.mob_jar.desc.full", mobName));
            }
        } else {
            tooltip.add(StatCollector.translateToLocal("item.mob_jar.desc.empty"));
        }
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }
}
