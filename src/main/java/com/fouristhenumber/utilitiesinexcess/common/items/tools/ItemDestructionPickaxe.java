package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.DestructionPickaxeConfig;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

public class ItemDestructionPickaxe extends ItemPickaxe implements ITranslucentItem {

    public ItemDestructionPickaxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:destruction_pickaxe");
        setUnlocalizedName("destruction_pickaxe");
        if (DestructionPickaxeConfig.unbreakable) setMaxDamage(0);
    }

    public static final Set<BlockMeta> affectedBlockCache = new HashSet<>();

    public static void initializeCache() {
        if (DestructionPickaxeConfig.includeEffective != null) {
            for (String affectedBlockString : DestructionPickaxeConfig.includeEffective) {
                int meta;
                String affectedBlockName;

                if (affectedBlockString.contains("*")) {
                    String[] nameAndMeta = affectedBlockString.split("\\*");
                    affectedBlockName = nameAndMeta[0];
                    String sourceMetaString = nameAndMeta[1].toUpperCase();

                    meta = Integer.parseInt(sourceMetaString, 16);
                } else {
                    affectedBlockName = affectedBlockString;
                    meta = -1;
                }

                Block block = Block.getBlockFromName(affectedBlockName);

                if (block != null) {
                    affectedBlockCache.add(new BlockMeta(block, meta));
                }
            }
        }
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
        BlockMeta exactMatch = new BlockMeta(block, meta);
        BlockMeta wildcardMatch = new BlockMeta(block, -1);

        if (affectedBlockCache.contains(exactMatch) || affectedBlockCache.contains(wildcardMatch)) {
            return efficiencyOnProperMaterial * DestructionPickaxeConfig.effectiveSpeedModifier;
        } else {
            return efficiencyOnProperMaterial * DestructionPickaxeConfig.ineffectiveSpeedModifier;
        }
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
}
