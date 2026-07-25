package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.gtnewhorizons.postea.api.BlockAccessCompat;
import com.gtnewhorizons.postea.api.BlockReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class SpikeTransformation {

    private static int woodSpikeId = -1;
    private static int ironSpikeId = -1;
    private static int goldSpikeId = -1;
    private static int diamondSpikeId = -1;

    public static void registerIDResolvers() {
        BlockReplacementManager.registerIDResolver("ExtraUtilities:spike_base_wood", id -> woodSpikeId = id);
        BlockReplacementManager.registerIDResolver("ExtraUtilities:spike_base", id -> ironSpikeId = id);
        BlockReplacementManager.registerIDResolver("ExtraUtilities:spike_base_gold", id -> goldSpikeId = id);
        BlockReplacementManager.registerIDResolver("ExtraUtilities:spike_base_diamond", id -> diamondSpikeId = id);
    }

    public static BlockInfo transform(NBTTagCompound _oldTag, World world, Chunk chunk) {
        int blockId = BlockAccessCompat.getBlockIDAtTE(_oldTag, chunk);

        final Block block;
        if (blockId == woodSpikeId) block = ModBlocks.SPIKE_WOOD.get();
        else if (blockId == ironSpikeId) block = ModBlocks.SPIKE_IRON.get();
        else if (blockId == goldSpikeId) block = ModBlocks.SPIKE_GOLD.get();
        else if (blockId == diamondSpikeId) block = ModBlocks.SPIKE_DIAMOND.get();
        else return null;

        return new BlockInfo(block, 0, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT("TileEntitySpikeUIE", oldTag);

            ItemStack fakeWeaponStack = new ItemStack(Item.getItemFromBlock(block));
            if (oldTag.hasKey("ench")) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setTag(
                    "ench",
                    oldTag.getTagList("ench", 10)
                        .copy());
                fakeWeaponStack.setTagCompound(itemTag);
            }

            NBTTagCompound fakeWeapon = new NBTTagCompound();
            fakeWeaponStack.writeToNBT(fakeWeapon);
            tag.setTag("FakeWeapon", fakeWeapon);

            String spikeType = ((BlockSpike) block).getSpikeType()
                .name();
            tag.setString("SpikeType", spikeType);

            return tag;
        });
    }
}
