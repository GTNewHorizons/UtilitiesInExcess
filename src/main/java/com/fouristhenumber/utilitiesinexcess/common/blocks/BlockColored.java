package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintbrush;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.config.blocks.ColoredBlocksConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock_Client;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PaintbrushColorSelect;
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

        setHardness(base.getBlockHardness(null, 0, 0, 0));
        // This dumb ratio is due to the random scalars present in both get and set resistance.
        setResistance(base.getExplosionResistance(null) * (5f / 3f));
        setStepSound(base.stepSound);
        setBlockName(((AccessorBlock) base).uie$getUnlocalizedNameRaw() + "_colored");
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String textureName = ((AccessorBlock_Client) base).uie$getTextureName() + "_colored_grayscale";
        blockIcon = new BlockColoredTexture(textureName, base, colorMultiplier);

        if (!(reg instanceof TextureMap tm)) return;

        tm.setTextureEntry(textureName, (TextureAtlasSprite) blockIcon);
    }

    public static boolean shouldUsePaintBrush() {
        return Mods.EndlessIDs.isLoaded() && ColoredBlocksConfig.INSTANCE.enableDying;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if (shouldUsePaintBrush()) {
            list.add(new ItemStack(itemIn, 1, 0b0_11111_11111_11111));
        } else {
            for (int i = 0; i < 16; ++i) {
                list.add(new ItemStack(itemIn, 1, i));
            }
        }
    }

    public static int getRGBFromEIDMeta(int meta) {
        int red = (meta >> 7) & 0b11111000;
        int green = (meta >> 2) & 0b11111000;
        int blue = (meta << 3) & 0b11111000;

        return (red << 16) | (green << 8) | blue;
    }

    public static int getEIDMetaFromRGB(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return ((red << 7) & 0b0_11111_00000_00000) | ((green << 2) & 0b0_00000_11111_00000) | (blue >> 3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        if (shouldUsePaintBrush()) {
            return getRGBFromEIDMeta(worldIn.getBlockMetadata(x, y, z));
        }

        int meta = worldIn.getBlockMetadata(x, y, z);
        return ColorUtils.getHexColorFromWoolMeta(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return shouldUsePaintBrush() ? getRGBFromEIDMeta(meta) : ColorUtils.getHexColorFromWoolMeta(meta);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemPaintbrush) {
            PacketHandler.INSTANCE
                .sendToServer(new PaintbrushColorSelect(getRGBFromEIDMeta(world.getBlockMetadata(x, y, z))));
            return null;
        }

        return super.getPickBlock(target, world, x, y, z, player);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return base.getFlammability(world, x, y, z, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return base.getFireSpreadSpeed(world, x, y, z, face);
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

            if (shouldUsePaintBrush()) {
                name = StatCollector.translateToLocalFormatted("uie.colored_blocks.color.dyeable", name);
            } else {
                name = StatCollector
                    .translateToLocalFormatted("uie.colored_blocks.color." + stack.getItemDamage(), name);
            }
            return name;
        }

        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
            super.addInformation(stack, player, tooltip, p_77624_4_);
            tooltip.add("#" + Color.rgbToFullHexString(getRGBFromEIDMeta(stack.getItemDamage())));
            tooltip.add(StatCollector.translateToLocalFormatted("uie.colored_blocks.color.dyeable.desc"));
        }
    }
}
