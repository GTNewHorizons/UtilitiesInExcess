package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.AntiParticulateShovelConfig;

public class ItemAntiParticulateShovel extends ItemSpade {

    public ItemAntiParticulateShovel() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:anti_particulate_shovel");
        setUnlocalizedName("anti_particulate_shovel");
        if (AntiParticulateShovelConfig.unbreakable) setMaxDamage(0);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int x, int y, int z,
        EntityLivingBase harvester) {
        int worldHeight = worldIn.getHeight();
        if (AntiParticulateShovelConfig.breakFallingAbove) for (int curY = y + 1; curY < worldHeight; curY++) {
            Block block = worldIn.getBlock(x, curY, z);
            if (block instanceof BlockFalling f && this.func_150893_a(stack, block) >= 1) {
                EntityPlayer hPlayer = (EntityPlayer) harvester;
                int meta = worldIn.getBlockMetadata(x, y, z);
                if (block.removedByPlayer(worldIn, hPlayer, x, curY, z, true))
                    block.harvestBlock(worldIn, hPlayer, x, curY, z, meta);
            } else break;
        }
        return super.onBlockDestroyed(stack, worldIn, blockIn, x, y, z, harvester);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (!ItemConfig.shiftForDescription || GuiScreen.isShiftKeyDown()) {
            tooltip
                .add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.anti_particulate_shovel.desc.1"));
            tooltip
                .add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.anti_particulate_shovel.desc.2"));
        } else tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("shift_for_description"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (AntiParticulateShovelConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (AntiParticulateShovelConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (AntiParticulateShovelConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
