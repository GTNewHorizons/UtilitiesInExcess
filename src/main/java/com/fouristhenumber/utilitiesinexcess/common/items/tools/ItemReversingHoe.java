package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.ReversingHoeConfig;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import cpw.mods.fml.common.registry.GameRegistry;

// TODO: Add new features to the reversing hoe
public class ItemReversingHoe extends ItemHoe implements ITranslucentItem {

    public static final HashMap<BlockMeta, BlockMeta> blockConversionCache = new HashMap<>();

    public ItemReversingHoe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:reversing_hoe");
        setUnlocalizedName("reversing_hoe");
        if (ReversingHoeConfig.unbreakable) setMaxDamage(0);
    }

    public static void initializeCache() {
        if (ReversingHoeConfig.blockTransformations != null) {
            for (String transformation : ReversingHoeConfig.blockTransformations) {
                if (transformation == null) continue;
                if (!transformation.contains("->")) {
                    UtilitiesInExcess.LOG
                        .warn("Reversing Hoe Config: {} does not contain '->', skipped", transformation);
                    continue;
                }
                try {
                    String[] parts = transformation.split("->");

                    String[] sourceSplit = parts[0].trim()
                        .split(":");
                    String sourceDomain = sourceSplit[0];
                    String sourceName = sourceSplit[1];
                    int sourceMeta = sourceSplit.length == 3 ? Integer.parseInt(sourceSplit[2]) : -1;

                    String[] targetSplit = parts[1].trim()
                        .split(":");
                    String targetDomain = targetSplit[0];
                    String targetName = targetSplit[1];
                    int targetMeta = targetSplit.length == 3 ? Integer.parseInt(targetSplit[2]) : 0;

                    Block sourceBlock = GameRegistry.findBlock(sourceDomain, sourceName);
                    Block targetBlock = GameRegistry.findBlock(targetDomain, targetName);

                    if (sourceBlock == null || targetBlock == null) {
                        UtilitiesInExcess.LOG.warn("Reversing Hoe Config: Could not find {}, skipped", transformation);
                        continue;
                    }

                    blockConversionCache
                        .put(new BlockMeta(sourceBlock, sourceMeta), new BlockMeta(targetBlock, targetMeta));

                } catch (Exception e) {
                    UtilitiesInExcess.LOG.warn("Reversing Hoe Config: Skipped malformed config: {}", transformation);
                }
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {

        Block block = world.getBlock(x, y, z);
        int metaInWorld = world.getBlockMetadata(x, y, z);

        BlockMeta exactKey = new BlockMeta(block, metaInWorld);
        BlockMeta wildcardKey = new BlockMeta(block, -1);

        BlockMeta targetData = null;

        if (blockConversionCache.containsKey(exactKey)) {
            targetData = blockConversionCache.get(exactKey);
        } else if (blockConversionCache.containsKey(wildcardKey)) {
            targetData = blockConversionCache.get(wildcardKey);
        }

        if (targetData != null) {
            Block targetBlock = targetData.getBlock();
            int targetMeta = targetData.getBlockMeta();

            if (!world.isRemote) world.setBlock(x, y, z, targetBlock, targetMeta, 3);

            if (!ReversingHoeConfig.unbreakable) {
                if (!world.isRemote) itemStack.damageItem(1, player);
            }

            return true;
        }

        else if (block == Blocks.wheat || block == Blocks.potatoes || block == Blocks.carrots) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta > 0) {
                if (!world.isRemote) world.setBlockMetadataWithNotify(x, y, z, meta - 1, 3);
                return true;
            }
            return false;
        }

        return super.onItemUse(itemStack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (ReversingHoeConfig.unbreakable)
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("item.unbreakable.desc"));
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (ReversingHoeConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (ReversingHoeConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (ReversingHoeConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
