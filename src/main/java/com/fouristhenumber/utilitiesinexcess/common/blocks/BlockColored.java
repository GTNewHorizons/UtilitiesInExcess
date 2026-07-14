package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.Color;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.IUIERegistered;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemPaintRoller;
import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.chromatictooltips.ColorChromaticTooltip;
import com.fouristhenumber.utilitiesinexcess.compat.endlessids.EIDsHelper;
import com.fouristhenumber.utilitiesinexcess.config.blocks.ColoredBlocksConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorBlock;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PaintRollerColorSelect;
import com.fouristhenumber.utilitiesinexcess.render.BlockColoredTexture;
import com.fouristhenumber.utilitiesinexcess.utils.ColorUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// These are intentionally not ore dictionary'd to their originals
// to avoid flooding nei crafting recipes with them.
public class BlockColored extends Block implements IUIERegistered {

    protected BaseBlock base;
    private String baseModID;
    private String baseName;
    private boolean initialized = true;
    private IIcon[] icons;
    private int baseMeta = 0;
    private String displayNameOverride = null;

    private final float brightnessMultiplier;
    public static final float DEFAULT_BRIGHTNESS = 1.5f;
    public static final ArrayList<BlockColored> COLORED_BLOCKS = new ArrayList<>();
    private static final HashMap<BaseBlock, BlockColored> BASES_TO_COLORED = new HashMap<>();

    public BlockColored(Block base) {
        this(base, DEFAULT_BRIGHTNESS);
    }

    public BlockColored(Block base, float brightnessMultiplier) {
        this(base, brightnessMultiplier, 0);
    }

    public BlockColored(Block base, float brightnessMultiplier, int baseMeta) {
        super(base.getMaterial());
        this.base = baseOf(base, baseMeta);
        this.brightnessMultiplier = brightnessMultiplier;
        this.baseMeta = baseMeta;

        setHardness(base.getBlockHardness(null, 0, 0, 0));
        // This dumb ratio is due to the random scalars present in both get and set resistance.
        setResistance(base.getExplosionResistance(null) * (5f / 3f));
        setStepSound(base.stepSound);
        setBlockName("colored_" + ((AccessorBlock) base).uie$getUnlocalizedNameRaw());

        COLORED_BLOCKS.add(this);
        BASES_TO_COLORED.put(baseOf(base, baseMeta), this);
    }

    // Config colored blocks initialization:
    // Pre-Init: registerConfigBlocks() parses the config, then constructs and registers instances
    // MixinTextureMap_ColoredBlocks (loader state is available, after post init events): initColoredBlocks() finds base
    // blocks, register their recipes, and registers them with the atlas
    // If angelica is present: initColoredBlocks() finds base blocks during CTM registration, mixin only registers with
    // atlas
    // On load complete: try finding base blocks again, for dedicated servers
    public BlockColored(String baseModID, String baseName, int baseMeta, float brightnessMultiplier) {
        super(Material.rock);
        this.brightnessMultiplier = brightnessMultiplier;
        this.baseModID = baseModID;
        this.baseName = baseName;
        this.baseMeta = baseMeta;
        this.initialized = false;

        setBlockName("colored_" + baseModID + "_" + baseName + "_" + baseMeta);
    }

    public void initFromString() {
        if (initialized) {
            return;
        }

        if (base == null) {
            base = baseOf(GameRegistry.findBlock(baseModID, baseName), baseMeta);
        }

        if (base.getBlock() == null) {
            throw new IllegalArgumentException(
                "Utilities in Excess - Colored Blocks: Block \"" + baseName
                    + "\" from mod ID \""
                    + baseModID
                    + "\" not found. Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
        }

        ((AccessorBlock) this).uie$setBlockMaterial(
            base.getBlock()
                .getMaterial());
        this.canBlockGrass = !base.getBlock()
            .getMaterial()
            .getCanBlockGrass();

        setHardness(
            base.getBlock()
                .getBlockHardness(null, 0, 0, 0));
        // This dumb ratio is due to the random scalars present in both get and set resistance.
        setResistance(
            base.getBlock()
                .getExplosionResistance(null) * (5f / 3f));
        setStepSound(base.getBlock().stepSound);

        opaque = isOpaqueCube();

        initialized = true;

        BASES_TO_COLORED.put(base, this);
    }

    // Util methods
    public static boolean allowDyingBlocks() {
        return Mods.EndlessIDs.isLoaded() && ColoredBlocksConfig.INSTANCE.enablePaintRoller
            && ColoredBlocksConfig.INSTANCE.enableDying;
    }

    public static int getRGBFromEIDMetaWithExtraBit(int meta) {
        int extra = (getExtraMetaBit(meta) << 9) & 0x01000000;

        return extra | getRGBFromEIDMeta(meta);
    }

    public static int getRGBFromEIDMeta(int meta) {
        int red = (meta >> 7) & 0b11111000;
        int green = (meta >> 2) & 0b11111000;
        int blue = (meta << 3) & 0b11111000;

        return (red << 16) | (green << 8) | blue;
    }

    public static int getEIDMetaFromRGBWithExtraBit(int rgb) {
        int extraBit = (rgb >> 24) & 1;

        return getEIDMetaFromRGB(rgb) | (extraBit << 15);
    }

    public static int getEIDMetaFromRGB(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return ((red << 7) & 0b0_11111_00000_00000) | ((green << 2) & 0b0_00000_11111_00000) | (blue >> 3);
    }

    public static int getExtraMetaBit(int meta) {
        return meta & 0b1_00000_00000_00000;
    }

    public static void setExtraMetaBit(World world, int x, int y, int z, int curMeta, boolean value) {
        EIDsHelper.setBlockMeta(world, x, y, z, ((value ? 1 : 0) << 15) | (curMeta & 0b0_11111_11111_11111));
        world.markBlockForUpdate(x, y, z);
    }

    public static BlockColored getColoredVersion(Block block, int meta) {
        return BASES_TO_COLORED.get(baseOf(block, meta));
    }
    //

    // BlockColored-specific methods
    public BaseBlock getBase() {
        return base;
    }

    public float getBrightnessMultiplier() {
        return brightnessMultiplier;
    }

    public boolean ignoreBaseMeta() {
        return false;
    }

    public boolean usesExtraBit() {
        return false;
    }

    public boolean useDefaultNEIPage() {
        return true;
    }

    public void setDisplayNameOverride(String displayName) {
        this.displayNameOverride = displayName;
    }

    public String getDisplayNameOverride() {
        return displayNameOverride;
    }

    public String getCustomNEIPage() {
        return null;
    }

    // IUIEModBLock
    private String registryName;

    @Override
    public void setRegistryName(String name) {
        registryName = name;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    // Item
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
            BlockColored bc = (BlockColored) field_150939_a;
            String name = bc.getDisplayNameOverride();
            if (name == null) {
                stack.setItemDamage(
                    bc.getBase()
                        .getMeta());
                name = Item.getItemFromBlock(
                    bc.getBase()
                        .getBlock())
                    .getItemStackDisplayName(stack);
                stack.setItemDamage(dmg);
            }

            if (allowDyingBlocks()) {
                name = StatCollector.translateToLocalFormatted("uie.colored_blocks.color.dyeable", name);
            } else {
                name = StatCollector
                    .translateToLocalFormatted("uie.colored_blocks.color." + stack.getItemDamage(), name);
            }
            return name;
        }

        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
            super.addInformation(stack, player, tooltip, p_77624_4_);
            if (allowDyingBlocks()) {
                if (Mods.ChromaticTooltips.isLoaded()) {
                    tooltip.add(ColorChromaticTooltip.makeTooltip(getRGBFromEIDMeta(stack.getItemDamage())));
                } else {
                    tooltip.add("#" + Color.rgbToFullHexString(getRGBFromEIDMeta(stack.getItemDamage())));
                }
            }
        }
    }

    // Config colored blocks handling
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
            int blockMeta;
            try {
                blockMeta = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Utilities in Excess - Colored Blocks: Couldn't parse brightness value \"" + args[2]
                        + "\" for extra colored blocks entry \""
                        + configString
                        + "\". Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
            }
            float brightness;
            try {
                brightness = Float.parseFloat(args[3]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Utilities in Excess - Colored Blocks: Couldn't parse brightness value \"" + args[3]
                        + "\" for extra colored blocks entry \""
                        + configString
                        + "\". Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
            }
            BlockColored newBlock = switch (args[4].toLowerCase()) {
                case "default" -> new BlockColored(blockDomain, blockName, blockMeta, brightness);
                case "ctm" -> new BlockColoredCTM(blockDomain, blockName, blockMeta, brightness);
                case "rotatable" -> new BlockColoredRotatable(blockDomain, blockName, blockMeta, brightness);
                case "light" -> new BlockColoredWithLight(blockDomain, blockName, blockMeta, brightness);
                default -> throw new IllegalArgumentException(
                    "Utilities in Excess - Colored Blocks: Couldn't parse type value \"" + args[4]
                        + "\" for extra colored blocks entry \""
                        + configString
                        + "\". Please check the \"extraColoredBlocks\" option in your Utilities in Excess config.");
            };
            Class<? extends ItemBlock> itemClass = switch (args[4].toLowerCase()) {
                case "ctm" -> BlockColoredCTM.ItemBlockColoredCTM.class;
                case "light" -> BlockColoredWithLight.ItemBlockColoredWithLight.class;
                default -> BlockColored.ItemBlockColored.class;
            };

            if (args.length >= 6) {
                newBlock.setDisplayNameOverride(args[5]);
            }

            newBlock.setCreativeTab(UtilitiesInExcess.uieTab);
            CONFIG_COLORED_BLOCKS.add(newBlock);
            COLORED_BLOCKS.add(newBlock);
            newBlock.setRegistryName(((AccessorBlock) newBlock).uie$getUnlocalizedNameRaw());
            GameRegistry.registerBlock(newBlock, itemClass, newBlock.getRegistryName());
        }
    }

    private static boolean recipesLoaded = false;

    public static void initColoredBlocks(Map<String, TextureAtlasSprite> mapRegisteredSprites) {
        for (BlockColored blockColored : COLORED_BLOCKS) {
            blockColored.initFromString();
            if (mapRegisteredSprites != null) {
                blockColored.actuallyRegisterBlockIcons(mapRegisteredSprites);
            }
        }
        if (!recipesLoaded) {
            RecipeLoader.loadColoredBlockRecipes();
            recipesLoaded = true;
        }
    }

    protected void actuallyRegisterBlockIcons(Map<String, TextureAtlasSprite> mapRegisteredSprites) {

        IIcon[] icons = new IIcon[6];
        HashSet<String> names = new HashSet<>();

        for (int i = 0; i < 6; i++) {
            icons[i] = base.getBlock()
                .getIcon(i, this.baseMeta);
            names.add(icons[i].getIconName());
        }

        HashMap<String, IIcon> iconMap = new HashMap<>();
        for (String name : names) {
            String textureName = UtilitiesInExcess.MODID + ":" + name.replace(':', '_') + "_colored_grayscale";
            BlockColoredTexture icon = new BlockColoredTexture(textureName, name, this, brightnessMultiplier);
            mapRegisteredSprites.put(textureName, icon);
            iconMap.put(name, icon);
        }

        for (int i = 0; i < 6; i++) {
            icons[i] = iconMap.get(icons[i].getIconName());
        }

        if (this instanceof BlockColoredRotatable rotatable) {
            for (int i = 0; i < 6; i++) {
                rotatable.iconsRotated[i] = base.getBlock()
                    .getIcon(i, baseMeta + 1 < 16 ? baseMeta + 1 : baseMeta - 1);
                rotatable.iconsRotated[i] = iconMap.get(rotatable.iconsRotated[i].getIconName());
            }
        }

        this.icons = icons;
        this.blockIcon = icons[0];
    }

    // Implementations of Block methods
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {}

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        if (allowDyingBlocks()) {
            return getRGBFromEIDMeta(worldIn.getBlockMetadata(x, y, z));
        }

        int meta = worldIn.getBlockMetadata(x, y, z);
        return ColorUtils.getHexColorFromWoolMeta(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return allowDyingBlocks() ? getRGBFromEIDMeta(meta) : ColorUtils.getHexColorFromWoolMeta(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (icons != null) {
            return icons[side];
        }

        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if (allowDyingBlocks()) {
            list.add(new ItemStack(itemIn, 1, 0b0_11111_11111_11111));
        } else {
            for (int i = 0; i < 16; ++i) {
                list.add(new ItemStack(itemIn, 1, i));
            }
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemPaintRoller) {
            PacketHandler.INSTANCE.sendToServer(
                new PaintRollerColorSelect(
                    usesExtraBit() ? getRGBFromEIDMetaWithExtraBit(world.getBlockMetadata(x, y, z))
                        : getRGBFromEIDMeta(world.getBlockMetadata(x, y, z))));
            return null;
        }

        // TODO revert if https://github.com/GTMEGA/EndlessIDs/issues/291 is solved
        ItemStack result = super.getPickBlock(target, world, x, y, z, player);
        result.setItemDamage(result.getItemDamage() & 0b0_11111_11111_11111);

        return result;
        // return super.getPickBlock(target, world, x, y, z, player);
    }

    @Override
    public int damageDropped(int meta) {
        // TODO revert if https://github.com/GTMEGA/EndlessIDs/issues/291 is solved
        return meta & 0b0_11111_11111_11111;
        // return meta;
    }

    // Redirects of Block methods
    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return getBase().getBlock()
            .getFlammability(world, x, y, z, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return getBase().getBlock()
            .getFireSpreadSpeed(world, x, y, z, face);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return (getBase() == null || getBase().getBlock() == null) ? super.getRenderBlockPass()
            : getBase().getBlock()
                .getRenderBlockPass();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return (getBase() == null || getBase().getBlock() == null) ? super.renderAsNormalBlock()
            : getBase().getBlock()
                .renderAsNormalBlock();
    }

    @Override
    public boolean isOpaqueCube() {
        return (getBase() == null || getBase().getBlock() == null) ? super.isOpaqueCube()
            : getBase().getBlock()
                .isOpaqueCube();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return (getBase() == null || getBase().getBlock() == null) ? super.shouldSideBeRendered(worldIn, x, y, z, side)
            : getBase().getBlock()
                .shouldSideBeRendered(worldIn, x, y, z, side) && worldIn.getBlock(x, y, z) != this;
    }

    @Override
    public int getLightOpacity() {
        return (getBase() == null || getBase().getBlock() == null) ? super.getLightOpacity()
            : getBase().getBlock()
                .getLightOpacity();
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return (getBase() == null || getBase().getBlock() == null) ? super.getLightOpacity(world, x, y, z)
            : getBase().getBlock()
                .getLightOpacity(world, x, y, z);
    }
    //

    public static BaseBlock baseOf(Block block, int meta) {
        return new BaseBlock(block, meta);
    }

    public static class BaseBlock {

        private final Block block;
        private final int meta;

        public BaseBlock(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        public Block getBlock() {
            return block;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof BaseBlock bb) {
                return bb.getBlock() == block && bb.getMeta() == meta;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return getBlock().hashCode() + getMeta();
        }
    }
}
