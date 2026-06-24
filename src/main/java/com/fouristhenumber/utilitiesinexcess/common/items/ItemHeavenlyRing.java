package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.FakePlayer;

import com.fouristhenumber.utilitiesinexcess.ClientProxy;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@EventBusSubscriber()
@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class ItemHeavenlyRing extends Item implements IBauble {

    private final int RING_COUNT;
    private final String SUFFIX;

    public final IIcon[] wingIcons;

    public ItemHeavenlyRing(String suffix, int variants) {
        RING_COUNT = variants;
        SUFFIX = suffix;

        wingIcons = new IIcon[RING_COUNT];

        setTextureName("utilitiesinexcess:heavenly_ring_" + suffix);
        setUnlocalizedName("heavenly_ring_" + suffix);
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int meta = stack.getItemDamage();
        if (meta == RING_COUNT - 1) {
            stack.setItemDamage(0);
        } else {
            stack.setItemDamage(meta + 1);
        }
        if (world.isRemote) {
            player.addChatMessage(
                new ChatComponentTranslation(
                    "chat.heavenly_ring_modify",
                    StatCollector.translateToLocal("item.heavenly_ring_" + SUFFIX + ".type." + stack.getItemDamage())));
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        for (int i = 0; i < RING_COUNT; ++i) {
            wingIcons[i] = register.registerIcon(this.getIconString() + ".wing." + i);
        }
        super.registerIcons(register);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(
            EnumChatFormatting.GRAY
                + StatCollector.translateToLocal("item.heavenly_ring_" + SUFFIX + ".type." + stack.getItemDamage()));
        tooltip.add(StatCollector.translateToLocal("item.heavenly_ring.desc"));
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

    public static Map<EntityPlayer, ItemStack> wingedPlayers = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) {
        if (ClientProxy.frameCount % 40 > 1) return;

        EntityPlayer player = event.entityPlayer;

        boolean hasRing = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemHeavenlyRing) {
                hasRing = true;
                wingedPlayers.putIfAbsent(player, stack);
                break;
            }
        }

        if (!hasRing) {
            wingedPlayers.remove(player);
        }
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
