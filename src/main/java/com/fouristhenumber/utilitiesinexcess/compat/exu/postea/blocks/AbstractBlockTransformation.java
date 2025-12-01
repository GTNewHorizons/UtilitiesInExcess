package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.DummyBlock;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.gtnewhorizons.postea.api.BlockReplacementManager;
import com.gtnewhorizons.postea.api.ItemStackReplacementManager;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

import cpw.mods.fml.common.registry.GameRegistry;

public abstract class AbstractBlockTransformation implements IPosteaTransformation {

    private final Block dummyBlock;

    private String dummyName;
    private String oldName;

    public AbstractBlockTransformation() {
        dummyBlock = new DummyBlock(Material.ground);
    }

    @Override
    public void registerDummies() {
        GameRegistry.registerBlock(dummyBlock, dummyName);
    }

    @Override
    public void addItemRemappings(Map<String, Item> remappings) {
        remappings.put(oldName, Item.getItemFromBlock(dummyBlock));
    }

    @Override
    public void addBlockRemappings(Map<String, Block> remappings) {
        remappings.put(oldName, dummyBlock);
    }

    @Override
    public void registerTransformations() {
        BlockReplacementManager
            .addBlockReplacement(UtilitiesInExcess.MODID + ":" + dummyName, this::doBlockTransformation);
        ItemStackReplacementManager
            .addItemReplacement(UtilitiesInExcess.MODID + ":" + dummyName, this::doItemTransformation);
    }

    public abstract NBTTagCompound doItemTransformation(NBTTagCompound tag);

    public abstract BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world);

    public void setDummyName(String dummyName) {
        this.dummyName = dummyName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
