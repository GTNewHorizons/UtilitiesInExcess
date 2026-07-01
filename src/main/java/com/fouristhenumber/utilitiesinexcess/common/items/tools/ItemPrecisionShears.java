package com.fouristhenumber.utilitiesinexcess.common.items.tools;

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
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.PrecisionShearsConfig;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemPrecisionShears extends ItemShears implements ITranslucentItem {

    public static final String COOLDOWN_NBT_TAG = "uie:cooldown";
    private IIcon cooldownIcon;

    public ItemPrecisionShears() {
        setTextureName("utilitiesinexcess:precision_shears");
        setUnlocalizedName("precision_shears");
        if (PrecisionShearsConfig.INSTANCE.unbreakable) setMaxDamage(0);
        int harvestLevel = PrecisionShearsConfig.INSTANCE.toolLevel;
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
        float hardness = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        int miningLevel = block.getHarvestLevel(meta);
        if (!player.isSneaking() || nbt.getInteger(COOLDOWN_NBT_TAG) != 0 || hardness <= 0 || miningLevel >= 2)
            return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
        if (!world.isRemote && block.removedByPlayer(world, player, x, y, z, true)) {
            block.harvestBlock(world, player, x, y, z, meta);
            nbt.setInteger(COOLDOWN_NBT_TAG, PrecisionShearsConfig.INSTANCE.cooldown);
            itemStack.setTagCompound(nbt);
            if (!PrecisionShearsConfig.INSTANCE.unbreakable) itemStack.damageItem(1, player);
        } else if (PrecisionShearsConfig.INSTANCE.spawnParticles) {
            world.spawnParticle("smoke", x + 0.5d, y + 0.5d, z + 0.5d, 0.0D, 0.0D, 0.0D);
        }
        return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (ForgeHooks.isToolEffective(itemstack, block, metadata)) {
            return PrecisionShearsConfig.INSTANCE.efficiency;
        } else {
            return super.getDigSpeed(itemstack, block, metadata);
        }
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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (PrecisionShearsConfig.INSTANCE.unbreakable)
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("item.unbreakable.desc"));
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (PrecisionShearsConfig.INSTANCE.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (PrecisionShearsConfig.INSTANCE.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (PrecisionShearsConfig.INSTANCE.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return PrecisionShearsConfig.INSTANCE.enable;
        }

        @SubscribeEvent
        public static void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
            if (event.harvester == null) return;
            if (event.harvester.getHeldItem() == null) return;

            if (event.harvester.getHeldItem()
                .getItem() instanceof ItemPrecisionShears) {
                EntityPlayer player = event.harvester;

                AxisAlignedBB dropSearchArea = AxisAlignedBB
                    .getBoundingBox(event.x - 1, event.y - 1, event.z - 1, event.x + 1, event.y + 1, event.z + 1);
                List<EntityItem> foundItems = event.world.getEntitiesWithinAABBExcludingEntity(player, dropSearchArea)
                    .stream()
                    .filter(EntityItem.class::isInstance)
                    .map(EntityItem.class::cast)
                    .filter(entityItem -> entityItem.age == 0)
                    .collect(Collectors.toList());
                for (EntityItem item : foundItems) {
                    if (!event.world.isRemote) item.setPosition(player.posX, player.posY, player.posZ);
                }

                for (ItemStack drop : event.drops) {
                    if (!player.inventory.addItemStackToInventory(drop)) {
                        // Not player.entityDropItem(drop, 0.0f); cause i don't want the pickup delay:P
                        EntityItem entityitem = new EntityItem(
                            player.worldObj,
                            player.posX,
                            player.posY,
                            player.posZ,
                            drop);
                        player.worldObj.spawnEntityInWorld(entityitem);
                    }
                }
                player.inventoryContainer.detectAndSendChanges();
                event.drops.clear();
            }
        }
    }
}
