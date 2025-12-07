package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.DummyBlock;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.SoundMufflerTransformation;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;

/**
 * A simple {@link IPosteaTransformation} that transforms a single tile entity.
 * Extend and use setOldBlockName and setNewBlock
 * then do your transformation in doTileEntityTransformation
 * <p>
 * See {@link SoundMufflerTransformation} for an example.
 */
public abstract class AbstractTileEntityTransformation implements IPosteaTransformation {

    private final Block dummyBlock;

    private String oldBlockName;
    private String oldTEID;

    private ModBlocks block;

    /**
     * Set the *full registry name* (e.g. "modid:item") of the old block this transformation is replacing
     *
     * @param oldName The registry name for the block this transformation is replacing
     */
    public void setOldBlockName(String oldName) {
        this.oldBlockName = oldName;
    }

    /**
     * Set the Modblock instance of the block that this TE will be re-mapped to.
     *
     * @param block The new block to be mapped to
     */
    public void setNewBlock(ModBlocks block) {
        this.block = block;
    }

    /**
     * Set the tile entity ID of the tile entity this transformation is replacing
     *
     * @param oldId The tile entity ID for the tile entity this transformation is replacing
     */
    public void setOldTileEntityId(String oldId) {
        this.oldTEID = oldId;
    }

    public AbstractTileEntityTransformation() {
        dummyBlock = new DummyBlock(Material.ground);
    }

    @Override
    public void addItemRemappings(Map<String, Item> remappings) {
        remappings.put(oldBlockName, block.getItem());
    }

    @Override
    public void addBlockRemappings(Map<String, Block> remappings) {
        remappings.put(oldBlockName, block.get());
    }

    @Override
    public void registerTransformations() {
        TileEntityReplacementManager.tileEntityTransformer(oldTEID, this::doTileEntityTransformation);
    }

    public abstract BlockInfo doTileEntityTransformation(NBTTagCompound oldTag, World world, Chunk chunk);
}
