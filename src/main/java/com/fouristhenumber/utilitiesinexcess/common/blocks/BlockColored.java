package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock;
import com.fouristhenumber.utilitiesinexcess.render.BlockColoredTexture;
import com.fouristhenumber.utilitiesinexcess.utils.ColorUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// These are intentionally not ore dictionary'd to their originals
// to avoid flooding nei crafting recipes with them.
public class BlockColored extends Block {

    private final Block base;
    private final float colorMultiplier;

    public BlockColored(Block base) {
        this(base, 1.5F);
    }

    public BlockColored(Block base, float colorMultiplier) {
        super(base.getMaterial());
        this.base = base;
        this.colorMultiplier = colorMultiplier;

        setStepSound(base.stepSound);
        setBlockName(((AccessorBlock) base).uie$getTextureName() + "_colored");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String textureName = ((AccessorBlock) base).uie$getTextureName() + "_colored_grayscale";
        blockIcon = new BlockColoredTexture(textureName, base, colorMultiplier);

        if (!(reg instanceof TextureMap tm)) return;

        tm.setTextureEntry(textureName, (TextureAtlasSprite) blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 16; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        return ColorUtils.getHexColorFromWoolMeta(meta);
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return ColorUtils.getHexColorFromWoolMeta(meta);
    }

    public Block getBase() {
        return base;
    }

    public static class ItemBlockColored extends ItemBlock {

        public ItemBlockColored(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            int dmg = stack.getItemDamage();
            stack.setItemDamage(0);
            String name = Item.getItemFromBlock(((BlockColored) field_150939_a).base)
                .getItemStackDisplayName(stack);
            stack.setItemDamage(dmg);

            name = StatCollector.translateToLocalFormatted("uie.colored_blocks.color." + stack.getItemDamage(), name);
            return name;
        }
    }
}
