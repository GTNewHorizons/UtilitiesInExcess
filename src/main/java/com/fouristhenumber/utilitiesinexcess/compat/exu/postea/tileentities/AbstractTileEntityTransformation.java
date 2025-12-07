package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.DummyBlock;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks.SoundMufflerTransformation;
import com.fouristhenumber.utilitiesinexcess.utils.UIEUtils;
import com.gtnewhorizons.postea.api.ItemStackReplacementManager;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * A simple {@link IPosteaTransformation} that transforms a single block.
 * Extend and use setDummyName and setOldName
 * then do your transformations in doItemTransformation and doBlockTransformation
 * <p>
 * See {@link SoundMufflerTransformation} for an example.
 */
public abstract class AbstractTileEntityTransformation implements IPosteaTransformation {

    private final Block dummyBlock;

    private String dummyName;
    private String oldBlockName;
    private String oldTEID;

    /**
     * Set the name for this transformation's dummy block *without* the modid, e.g. "dummy_golden_bag".
     *
     * @param dummyName The registry name for this transformation's dummy block
     */
    public void setDummyBlockName(String dummyName) {
        this.dummyName = dummyName;
    }

    /**
     * Set the *full registry name* (e.g. "modid:item") of the old block this transformation is replacing
     *
     * @param oldName The registry name for the block this transformation is replacing
     */
    public void setOldBlockName(String oldName) {
        this.oldBlockName = oldName;
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
    public void registerDummies() {
        GameRegistry.registerBlock(dummyBlock, dummyName);
        UIEUtils.hideInNei(dummyBlock);
    }

    @Override
    public void addItemRemappings(Map<String, Item> remappings) {
        remappings.put(oldBlockName, Item.getItemFromBlock(dummyBlock));
    }

    @Override
    public void addBlockRemappings(Map<String, Block> remappings) {
        remappings.put(oldBlockName, dummyBlock);
    }

    @Override
    public void registerTransformations() {
        ItemStackReplacementManager
            .addItemReplacement(UtilitiesInExcess.MODID + ":" + dummyName, this::doItemTransformation);
        TileEntityReplacementManager.tileEntityTransformer(oldTEID, this::doTileEntityTransformation);
    }

    public abstract NBTTagCompound doItemTransformation(NBTTagCompound tag);

    public abstract BlockInfo doTileEntityTransformation(NBTTagCompound oldTag, World world, Chunk chunk);
}
