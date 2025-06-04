package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressedCobblestone;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockEtherealGlass;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFloating;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockTrashCanItem;
import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;

import cpw.mods.fml.common.registry.GameRegistry;

// Credit to Et Futurum (Requiem)
public enum ModBlocks {
    // spotless:off

    // make sure to leave a trailing comma
    FLOATING_BLOCK(BlockConfig.enableFloatingBlock, new BlockFloating(), BlockFloating.ItemBlockFloating.class, "floatingBlock"),
    COMPRESSED_COBBLESTONE(BlockConfig.enableCompressedCobblestone, new BlockCompressedCobblestone(), BlockCompressedCobblestone.ItemCompressedCobblestone.class, "compressedCobblestone"),
    REDSTONE_CLOCK(BlockConfig.enableRedstoneClock, new BlockRedstoneClock(), BlockRedstoneClock.ItemBlockRedstoneClock.class, "redstoneClock"),
    ETHEREAL_GLASS(BlockConfig.enableEtherealGlass, new BlockEtherealGlass(), BlockEtherealGlass.ItemBlockEtherealGlass.class, "etherealGlass"),
    TRASH_CAN_ITEM(BlockConfig.enableTrashCanItem, new BlockTrashCanItem(), BlockTrashCanItem.ItemBlockTrashCanItem.class, "trashCanItem"),
    ; // leave trailing semicolon
    // spotless:on

    public static final ModBlocks[] VALUES = values();

    public static void init() {
        for (ModBlocks block : VALUES) {
            if (block.isEnabled()) {
                if (block.getItemBlock() != null || !block.getHasItemBlock()) {
                    GameRegistry.registerBlock(block.get(), block.getItemBlock(), block.name);
                    // This part is used if the getItemBlock() is not ItemBlock.class, so we register a custom ItemBlock
                    // class as the ItemBlock
                    // It is also used if the getItemBlock() == null and getHasItemBlock() is false, meaning we WANT to
                    // register it as null, making the block have no inventory item.
                } else {
                    GameRegistry.registerBlock(block.get(), block.name);
                    // Used if getItemBlock() == null but getHasItemBlock() is true, registering it with a default
                    // inventory item.
                }
            }
        }
    }

    private final boolean isEnabled;
    private final Block theBlock;
    /**
     * null == default ItemBlock
     */
    private final Class<? extends ItemBlock> itemBlock;
    /**
     * Determines if we should register the block with an ItemBlock.
     * Set to false when the constructor that specifies the ItemBlock is specifically set to false.
     */
    private boolean hasItemBlock;
    private final String name;

    ModBlocks(Boolean enabled, Block block, String name) {
        this(enabled, block, null, name);
        hasItemBlock = true;
    }

    ModBlocks(Boolean enabled, Block block, Class<? extends ItemBlock> iblock, String name) {
        isEnabled = enabled;
        theBlock = block;
        itemBlock = iblock;
        hasItemBlock = iblock != null;
        this.name = name;
    }

    /**
     * If this is false, the block is initialized without an inventory item, or ItemBlock.
     */
    public boolean getHasItemBlock() {
        return hasItemBlock;
    }

    public Block get() {
        return theBlock;
    }

    public Class<? extends ItemBlock> getItemBlock() {
        return itemBlock;
    }

    public Item getItem() {
        return Item.getItemFromBlock(get());
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }
}
