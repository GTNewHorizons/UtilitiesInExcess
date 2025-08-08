package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.PrecisionShearsConfig;

public class ItemPrecisionShears extends ItemShears {

    public static final String COOLDOWN_NBT_TAG = "uie:cooldown";
    private IIcon cooldownIcon;

    public ItemPrecisionShears() {
        setTextureName("utilitiesinexcess:precision_shears");
        setUnlocalizedName("precision_shears");
        if (PrecisionShearsConfig.unbreakable) setMaxDamage(0);
        int harvestLevel = PrecisionShearsConfig.toolLevel;
        setHarvestLevel("pickaxe", harvestLevel);
        setHarvestLevel("axe", harvestLevel);
        setHarvestLevel("shovel", harvestLevel);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            int cooldown = nbt.getInteger(COOLDOWN_NBT_TAG);
            if (cooldown > 0) {
                nbt.setInteger(COOLDOWN_NBT_TAG, cooldown - 1);
                stack.setTagCompound(nbt);
            }
        }
        super.onUpdate(stack, worldIn, entityIn, p_77663_4_, p_77663_5_);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        NBTTagCompound nbt = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int harvestLevel = block.getHarvestLevel(meta);
        if (!player.isSneaking() || nbt.getInteger(COOLDOWN_NBT_TAG) != 0
            || harvestLevel > PrecisionShearsConfig.toolLevel
            || harvestLevel < 0)
            return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
        if (!world.isRemote && block.removedByPlayer(world, player, x, y, z, true)) {
            block.harvestBlock(world, player, x, y, z, meta);
            nbt.setInteger(COOLDOWN_NBT_TAG, PrecisionShearsConfig.cooldown);
            itemStack.setTagCompound(nbt);
            if (!PrecisionShearsConfig.unbreakable) itemStack.damageItem(1, player);
        } else if (PrecisionShearsConfig.spawnParticles) {
            world.spawnParticle("smoke", x + 0.5d, y + 0.5d, z + 0.5d, 0.0D, 0.0D, 0.0D);
        }
        return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (ForgeHooks.isToolEffective(itemstack, block, metadata)) {
            return PrecisionShearsConfig.efficiency;
        } else {
            return super.getDigSpeed(itemstack, block, metadata);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (!ItemConfig.shiftForDescription || GuiScreen.isShiftKeyDown()) {
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.1"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.2"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.3"));
        } else tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("shift_for_description"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        cooldownIcon = register.registerIcon("utilitiesinexcess:precision_shears_cooldown");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound()
                .getInteger(COOLDOWN_NBT_TAG) > 0) {
                return cooldownIcon;
            }
        }
        return super.getIcon(stack, pass);
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (PrecisionShearsConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (PrecisionShearsConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (PrecisionShearsConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
