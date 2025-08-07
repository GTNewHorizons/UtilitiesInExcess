package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemPrecisionShears extends ItemShears {

    private final float efficiencyOnProperMaterial;

    private static final String COOLDOWN_NBT_TAG = "uie:cooldown";
    private static final int COOLDOWN_TICKS = 20;

    private IIcon cooldownIcon;

    public ItemPrecisionShears() {
        setTextureName("utilitiesinexcess:precision_shears");
        setUnlocalizedName("precision_shears");

        setMaxDamage(0);
        setMaxStackSize(1);

        int harvestLevel = ToolMaterial.STONE.getHarvestLevel();
        efficiencyOnProperMaterial = ToolMaterial.STONE.getEfficiencyOnProperMaterial();

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

    // TODO: Should decide whether excess items should spit out of the player or the block's position
    // TODO: Should it swing your hand on right click?
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        if (player.isSneaking()) {
            NBTTagCompound nbt = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
            if (nbt.getInteger(COOLDOWN_NBT_TAG) == 0) {
                Block block = world.getBlock(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);
                int harvestLevel = block.getHarvestLevel(meta);
                if (harvestLevel <= 1) {
                    if (!world.isRemote) {
                        // Get drops that the block directly reports
                        ArrayList<ItemStack> directDrops = block.getDrops(world, x, y, z, meta, 0);

                        AxisAlignedBB dropSearchArea = AxisAlignedBB
                            .getBoundingBox(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);

                        // Save a list of existing items so we don't accidentally grab them in our search
                        List<EntityItem> existingItems = world
                            .getEntitiesWithinAABBExcludingEntity(player, dropSearchArea)
                            .stream()
                            .filter(EntityItem.class::isInstance)
                            .map(EntityItem.class::cast)
                            .collect(Collectors.toList());

                        world.setBlockToAir(x, y, z);

                        List<EntityItem> foundItems = world.getEntitiesWithinAABBExcludingEntity(player, dropSearchArea)
                            .stream()
                            .filter(EntityItem.class::isInstance)
                            .map(EntityItem.class::cast)
                            .filter(entityItem -> !existingItems.contains(entityItem))
                            .collect(Collectors.toList());

                        // Give player items
                        for (ItemStack drop : directDrops) {
                            if (!player.inventory.addItemStackToInventory(drop)) {
                                player.entityDropItem(drop, 0.0f);
                            }
                        }

                        for (EntityItem itemEntity : foundItems) {
                            ItemStack stack = itemEntity.getEntityItem();
                            if (!player.inventory.addItemStackToInventory(stack)) {
                                player.entityDropItem(stack, 0.0f);
                            }
                            world.removeEntity(itemEntity);
                        }

                        player.inventoryContainer.detectAndSendChanges();

                        nbt.setInteger(COOLDOWN_NBT_TAG, COOLDOWN_TICKS);

                        itemStack.setTagCompound(nbt);
                    } else {
                        world.spawnParticle("smoke", x + 0.5d, y + 0.5d, z + 0.5d, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
        return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (ForgeHooks.isToolEffective(itemstack, block, metadata)) {
            return efficiencyOnProperMaterial;
        } else {
            return super.getDigSpeed(itemstack, block, metadata);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.2"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.precision_shears.desc.3"));
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
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
    //
}
