package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.DestructionPickaxeConfig;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import akka.japi.Pair;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemDestructionPickaxe extends ItemPickaxe implements ITranslucentItem {

    public ItemDestructionPickaxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:destruction_pickaxe");
        setUnlocalizedName("destruction_pickaxe");
        if (DestructionPickaxeConfig.unbreakable) setMaxDamage(0);
    }

    private final static HashMap<String, Pattern> compiledPatterns = new HashMap<>();
    private final static HashMap<Pair<String, String>, Boolean> resultLookup = new HashMap<>();

    public static boolean blockMatches(String name, String pattern) {
        // Prob can be made cleaner
        if (!compiledPatterns.containsKey(name)) {
            String replaceWildcard = pattern.replace(".", "\\.")
                .replace("*", ".*?")
                .replace("(", "\\(")
                .replace(")", "\\)");
            compiledPatterns.put(pattern, Pattern.compile(replaceWildcard));
        }
        Pattern p = compiledPatterns.get(pattern);
        if (!resultLookup.containsKey(new Pair<>(pattern, name))) {
            boolean res = p.matcher(name)
                .matches();
            resultLookup.put(new Pair<>(pattern, name), res);
        }
        return resultLookup.get(new Pair<>(pattern, name));
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int x, int y, int z,
        EntityLivingBase harvester) {
        if (!worldIn.isRemote) {
            worldIn.setBlockToAir(x, y, z);
        }

        return super.onBlockDestroyed(stack, worldIn, blockIn, x, y, z, harvester);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        var i = DestructionPickaxeConfig.includeEffective;
        var w = DestructionPickaxeConfig.excludeEffective;
        var name = block.delegate.name();
        for (String s : w) if (blockMatches(name, s))
            return efficiencyOnProperMaterial * DestructionPickaxeConfig.ineffectiveSpeedModifier;
        for (String s : i) if (blockMatches(name, s))
            return efficiencyOnProperMaterial * DestructionPickaxeConfig.effectiveSpeedModifier;
        return efficiencyOnProperMaterial * DestructionPickaxeConfig.ineffectiveSpeedModifier;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (DestructionPickaxeConfig.unbreakable)
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("item.unbreakable.desc"));
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (DestructionPickaxeConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (DestructionPickaxeConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (DestructionPickaxeConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return DestructionPickaxeConfig.enable;
        }

        @SubscribeEvent
        public static void onBlockBroken(BlockEvent.HarvestDropsEvent event) {
            if (!DestructionPickaxeConfig.voidMinedBlock) return;
            if (event.harvester == null) return;
            if (event.harvester.getHeldItem() == null) return;

            if (event.harvester.getHeldItem()
                .getItem() instanceof ItemDestructionPickaxe) {
                event.drops.clear();
            }
        }
    }
}
