package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemAntiParticulateShovel extends ItemSpade {

    public ItemAntiParticulateShovel() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:anti_particulate_shovel");
        setUnlocalizedName("anti_particulate_shovel");
        setMaxDamage(0);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int x, int y, int z,
        EntityLivingBase harvester) {
        int worldHeight = worldIn.getHeight();
        int currY = y + 1;
        while (currY < worldHeight) {
            Block block = worldIn.getBlock(x, currY, z);
            if (block == Blocks.gravel || block == Blocks.sand) {
                worldIn.setBlockToAir(x, currY, z);
                currY++;
            } else {
                break;
            }
        }
        return super.onBlockDestroyed(stack, worldIn, blockIn, x, y, z, harvester);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.anti_particulate_shovel.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.anti_particulate_shovel.desc.2"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }
}
