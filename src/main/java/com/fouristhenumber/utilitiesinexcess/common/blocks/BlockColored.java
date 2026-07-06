package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintbrush;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.config.RecipeConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.ColoredBlocksConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock_Client;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PaintbrushColorSelect;
import com.fouristhenumber.utilitiesinexcess.render.BlockColoredTexture;
import com.fouristhenumber.utilitiesinexcess.utils.ColorUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// These are intentionally not ore dictionary'd to their originals
// to avoid flooding nei crafting recipes with them.
public class BlockColored extends Block {

    private Block base;
    private final float colorMultiplier;

    public static final float DEFAULT_BRIGHTNESS = 1.5f;

    public static final ArrayList<BlockColored> COLORED_BLOCKS = new ArrayList<>();

    public BlockColored(Block base) {
        this(base, DEFAULT_BRIGHTNESS);
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

        COLORED_BLOCKS.add(this);
    }

    private String baseModID;

    private String baseName;

    private boolean initialized = true;

    public String textureOverrideDomain;

    public String textureOverrideName;

    public BlockColored(String baseModID, String baseName, float colorMultiplier, @Nullable String textureDomain,
        @Nullable String textureName) {
        super(Material.rock);
        this.colorMultiplier = colorMultiplier;
        this.baseModID = baseModID;
        this.baseName = baseName;
        this.initialized = false;
        this.textureOverrideDomain = textureDomain;
        this.textureOverrideName = textureName;

        setBlockName(baseModID + "_" + baseName + "_colored");
    }

    public void initFromString() {
        if (base == null) {
            base = GameRegistry.findBlock(baseModID, baseName);
        }

        if (base == null) {
            throw new IllegalArgumentException(
                "Utilities in Excess - Colored Blocks: Block \"" + baseName
                    + "\" from mod ID \""
                    + baseModID
                    + "\" not found. Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
        }

        ((AccessorBlock) this).uie$setBlockMaterial(base.getMaterial());
        this.canBlockGrass = !base.getMaterial()
            .getCanBlockGrass();

        setHardness(base.getBlockHardness(null, 0, 0, 0));
        // This dumb ratio is due to the random scalars present in both get and set resistance.
        setResistance(base.getExplosionResistance(null) * (5f / 3f));
        setStepSound(base.stepSound);

        opaque = isOpaqueCube();

        initialized = true;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String textureName;
        if (base != null) {
            textureName = ((AccessorBlock_Client) base).uie$getTextureName() + "_colored_grayscale";
        } else {
            textureName = baseModID + "_" + baseName + "_colored_grayscale";
        }
        blockIcon = new BlockColoredTexture(textureName, this, colorMultiplier);

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
            if (shouldUsePaintBrush()) {
                tooltip.add("#" + Color.rgbToFullHexString(getRGBFromEIDMeta(stack.getItemDamage())));
            }
        }
    }

    public static final ArrayList<BlockColored> CONFIG_COLORED_BLOCKS = new ArrayList<>();

    public static void registerConfigBlocks() {
        for (int i = 0; i < ColoredBlocksConfig.INSTANCE.extraColoredBlocks.length; i++) {
            String configString = ColoredBlocksConfig.INSTANCE.extraColoredBlocks[i];
            String[] args = configString.split(";");

            if (args.length < 5) {
                throw new IllegalArgumentException(
                    "Utilities in Excess - Colored Blocks: Extra colored blocks entry \"" + configString
                        + "\" doesn't have enough values. Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
            }

            String blockDomain = args[0];
            String blockName = args[1];
            float brightness;
            try {
                brightness = Float.parseFloat(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Utilities in Excess - Colored Blocks: Couldn't parse brightness value \"" + args[2]
                        + "\" for extra colored blocks entry \""
                        + configString
                        + "\". Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
            }
            String textureDomain = args[3];
            String textureName = args[4];

            BlockColored newBlock = new BlockColored(blockDomain, blockName, brightness, textureDomain, textureName);
            newBlock.setCreativeTab(UtilitiesInExcess.uieTab);
            CONFIG_COLORED_BLOCKS.add(newBlock);
            COLORED_BLOCKS.add(newBlock);
            GameRegistry.registerBlock(
                newBlock,
                ItemBlockColored.class,
                ((AccessorBlock) newBlock).uie$getUnlocalizedNameRaw());

        }
    }

    public static void initColoredBlocks() {
        for (BlockColored blockColored : CONFIG_COLORED_BLOCKS) {
            blockColored.initFromString();
        }

        if (!BlockConfig.coloredBlocks.enableColoredBlocks || !RecipeConfig.enableColoredBlockRecipes) return;
        for (BlockColored block : BlockColored.COLORED_BLOCKS) {
            GameRegistry.addShapedRecipe(
                new ItemStack(block, 8, 0b0_11111_11111_11111),
                "bbb",
                "bdb",
                "bbb",
                'b',
                block.getBase(),
                'd',
                new ItemStack(Items.dye, 1, 15));

            GameRegistry.addShapedRecipe(
                new ItemStack(block.getBase(), 8),
                "bbb",
                "bdb",
                "bbb",
                'b',
                new ItemStack(block, 8, OreDictionary.WILDCARD_VALUE),
                'd',
                new ItemStack(Items.water_bucket));
        }
    }

    // Redirects of Block methods
    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return base.getFlammability(world, x, y, z, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return base.getFireSpreadSpeed(world, x, y, z, face);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return base.getRenderBlockPass();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return base.renderAsNormalBlock();
    }

    @Override
    public boolean isOpaqueCube() {
        return base == null ? super.isOpaqueCube() : base.isOpaqueCube();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        Block block = worldIn.getBlock(x, y, z);
        return base.shouldSideBeRendered(worldIn, x, y, z, side) && block != this;
    }

    @Override
    public int getLightOpacity() {
        return base.getLightOpacity();
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return base.getLightOpacity(world, x, y, z);
    }
    //
}
