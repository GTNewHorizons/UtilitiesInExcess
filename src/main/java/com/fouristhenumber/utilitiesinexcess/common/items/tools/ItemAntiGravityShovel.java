package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.AntiGravityShovelConfig;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.FloatingBlockParticlePacket;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ItemAntiGravityShovel extends ItemSpade implements ITranslucentItem {

    public ItemAntiGravityShovel() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:anti_gravity_shovel");
        setUnlocalizedName("anti_gravity_shovel");
        if (AntiGravityShovelConfig.INSTANCE.unbreakable) setMaxDamage(0);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int x, int y, int z,
        EntityLivingBase harvester) {
        int worldHeight = worldIn.getHeight();
        if (AntiGravityShovelConfig.INSTANCE.breakFallingAbove) for (int curY = y + 1; curY < worldHeight; curY++) {
            Block block = worldIn.getBlock(x, curY, z);
            if (block instanceof BlockFalling f && this.func_150893_a(stack, block) >= 1) {
                EntityPlayer hPlayer = (EntityPlayer) harvester;
                int meta = worldIn.getBlockMetadata(x, curY, z);
                NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(
                    worldIn.provider.dimensionId,
                    x,
                    curY,
                    z,
                    32);
                PacketHandler.INSTANCE.sendToAllAround(
                    new FloatingBlockParticlePacket(x, curY, z, Block.getIdFromBlock(block), meta),
                    targetPoint);
                if (block.removedByPlayer(worldIn, hPlayer, x, curY, z, true))
                    block.harvestBlock(worldIn, hPlayer, x, curY, z, meta);
            } else break;
        }
        return super.onBlockDestroyed(stack, worldIn, blockIn, x, y, z, harvester);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (AntiGravityShovelConfig.INSTANCE.unbreakable)
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("item.unbreakable.desc"));
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (AntiGravityShovelConfig.INSTANCE.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (AntiGravityShovelConfig.INSTANCE.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (AntiGravityShovelConfig.INSTANCE.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return AntiGravityShovelConfig.INSTANCE.enable;
        }

        @SubscribeEvent
        public static void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
            if (!AntiGravityShovelConfig.INSTANCE.voidMinedBlocks) return;
            if (event.harvester == null) return;
            if (event.harvester.getHeldItem() == null) return;

            if (event.harvester.getHeldItem()
                .getItem() instanceof ItemAntiGravityShovel) {
                event.drops.clear();
            }
        }
    }
}
