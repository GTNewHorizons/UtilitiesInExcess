package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.FakePlayer;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber()
@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class ItemHeavenlyRing extends Item implements IBauble {

    public ItemHeavenlyRing() {
        setTextureName("utilitiesinexcess:heavenly_ring");
        setUnlocalizedName("heavenly_ring");
        setMaxDamage(0);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.heavenlyRing.desc"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {

    }

    @Optional.Method(modid = "Baubles")
    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer player)) return;
        if (player instanceof FakePlayer) return;

        player.capabilities.allowFlying = true;
        player.sendPlayerAbilities();
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer player)) return;
        if (player instanceof FakePlayer) return;

        if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Optional.Method(modid = "Baubles")
    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @EventBusSubscriber.Condition
    public static boolean shouldEventBusSubscribe() {
        return !Mods.Baubles.isLoaded();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;

        boolean hasRing = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemHeavenlyRing) {
                hasRing = true;
                break;
            }
        }

        if (player.capabilities.allowFlying == hasRing) return;

        if (hasRing) {
            player.capabilities.allowFlying = true;
        } else {
            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
            }
        }
        player.sendPlayerAbilities();
    }
}
