package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.formatNumber;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInvertedIngot extends Item implements ITranslucentItem {

    public ItemInvertedIngot() {
        this.setTextureName("utilitiesinexcess:inverted_ingot");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public static final DamageSource INVERTED_INGOT = (new DamageSource("inverted_ingot")).setDamageBypassesArmor();

    private static boolean isUnstable(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemInvertedIngot
            && stack.getItemDamage() == 0
            && stack.hasTagCompound();
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean p_77663_5_) {
        if (!(entityIn instanceof EntityPlayer player)) return;

        if (!(player.openContainer instanceof ContainerWorkbench) || checkImplosion(stack, worldIn)) {
            resolveImplosion(stack, player, s -> player.inventory.setInventorySlotContents(slot, s));
        }

        super.onUpdate(stack, worldIn, entityIn, slot, p_77663_5_);
    }

    public static boolean checkImplosion(ItemStack item, World world) {
        if (!isUnstable(item)) return false;
        if (item.getItemDamage() == 0 && item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag.getBoolean("Crafted") && !tag.hasKey("CraftedAt")) {
                tag.setLong("CraftedAt", world.getTotalWorldTime());
                item.setTagCompound(tag);
            }

            long passed = world.getTotalWorldTime() - tag.getLong("CraftedAt");
            return passed > InversionConfig.INSTANCE.invertedIngotImplosionTimer;
        }
        return false;
    }

    private static void resolveImplosion(ItemStack stack, EntityPlayer player, Consumer<ItemStack> setStack) {
        if (!isUnstable(stack) || InversionConfig.INSTANCE.invertedIngotMode == InversionConfig.InversionMode.OFF)
            return;

        if (InversionConfig.INSTANCE.invertedIngotMode == InversionConfig.InversionMode.DECAY) {
            setStack.accept(ModItems.INVERTED_NUGGET.newItemStack());
        } else {
            setStack.accept(null);
            if (InversionConfig.INSTANCE.invertedIngotMode == InversionConfig.InversionMode.IMPLODE) {
                player.closeScreen();
                player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            case 0 -> "item.inverted_ingot";
            case 1 -> "item.inverted_ingot.stable";
            default -> "item.inverted_ingot.quasi_normalized";
        };
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        int meta = stack.getItemDamage();
        if (meta == 0) {
            if (InversionConfig.INSTANCE.invertedIngotMode != InversionConfig.InversionMode.OFF) {
                if (!stack.hasTagCompound()) {
                    tooltip.add(StatCollector.translateToLocal("uie.desc.item.inverted_ingot.c"));
                } else {
                    tooltip.add(StatCollector.translateToLocal("uie.desc.item.inverted_ingot.1"));
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag.hasKey("CraftedAt")) {
                        double passed = player.worldObj.getTotalWorldTime() - tag.getLong("CraftedAt");
                        double timeLeft = (InversionConfig.INSTANCE.invertedIngotImplosionTimer - passed) / 20D;
                        tooltip.add(
                            StatCollector.translateToLocalFormatted(
                                "uie.desc.item.inverted_ingot.2",
                                formatNumber(Math.max(0, timeLeft))));
                    } else {
                        tooltip.add(
                            StatCollector.translateToLocalFormatted(
                                "uie.desc.item.inverted_ingot.2",
                                InversionConfig.INSTANCE.invertedIngotImplosionTimer / 20));
                    }
                    tooltip.add(StatCollector.translateToLocal("uie.desc.item.inverted_ingot.3"));
                    tooltip.add(StatCollector.translateToLocal("uie.desc.item.inverted_ingot.4"));
                    tooltip.add(StatCollector.translateToLocal("uie.desc.item.inverted_ingot.5"));
                }
            }
        } else {
            if (meta == 1) tooltip.add(StatCollector.translateToLocalFormatted("uie.desc.item.inverted_ingot.stable"));
            else tooltip.add(StatCollector.translateToLocalFormatted("item.inverted_ingot.quasi_normalized.desc"));
        }
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return (stack.getItemDamage() == 0) ? 1 : 64;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[3];
        icons[0] = reg.registerIcon("utilitiesinexcess:inverted_ingot");
        icons[1] = reg.registerIcon("utilitiesinexcess:inverted_ingot_stable");
        icons[2] = reg.registerIcon("utilitiesinexcess:inverted_ingot_quasi_normalized");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }

    public static class InvertedNugget extends Item implements ITranslucentItem {

        public InvertedNugget() {
            setUnlocalizedName("inverted_nugget");
            setTextureName("utilitiesinexcess:inverted_nugget");
        }
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return InversionConfig.INSTANCE.enableInvertedIngot;
        }

        // Stack itself will be handled by EntitySpawnEvent
        @SubscribeEvent
        public static void onItemToss(ItemTossEvent event) {
            ItemStack stack = event.entityItem.getEntityItem();

            if (isUnstable(stack)
                && InversionConfig.INSTANCE.invertedIngotMode == InversionConfig.InversionMode.IMPLODE) {
                event.player.attackEntityFrom(INVERTED_INGOT, Float.MAX_VALUE);
            }

        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.player.openContainer instanceof ContainerWorkbench bench) {
                ItemStack cursorItem = event.player.inventory.getItemStack();
                if (checkImplosion(cursorItem, event.player.worldObj))
                    resolveImplosion(cursorItem, event.player, event.player.inventory::setItemStack);

                for (int i = 0; i < bench.craftMatrix.getSizeInventory(); i++) {
                    final int slot = i;
                    ItemStack stack = bench.craftMatrix.getStackInSlot(i);
                    if (checkImplosion(stack, event.player.worldObj))
                        resolveImplosion(stack, event.player, s -> bench.craftMatrix.setInventorySlotContents(slot, s));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    // TODO Add (side = Side.SERVER) to the EventBusSubscriber once
    // https://github.com/GTNewHorizons/GTNHLib/issues/410 is closed
    @EventBusSubscriber
    public static class EventsServer {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return InversionConfig.INSTANCE.enableInvertedIngot;
        }

        // Destroy non-stable inverted ingots dropped in world no matter what
        @SubscribeEvent
        public static void entityItemSpawnEvent(EntityJoinWorldEvent event) {
            // TODO Remove once side is added to EventBusSubscriber
            if (event.world.isRemote) return;

            if (event.entity instanceof EntityItem entityItem) {
                ItemStack stack = entityItem.getEntityItem();

                if (!isUnstable(stack)
                    || InversionConfig.INSTANCE.invertedIngotMode == InversionConfig.InversionMode.OFF) return;
                if (InversionConfig.INSTANCE.invertedIngotMode == InversionConfig.InversionMode.DECAY) {
                    entityItem.setEntityItemStack(ModItems.INVERTED_NUGGET.newItemStack(stack.stackSize));
                } else {
                    entityItem.setDead();
                    event.setCanceled(true);
                }
            }
        }
    }
}
