package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class FullChestTransformation extends AbstractTileEntityTransformation {

    public FullChestTransformation() {
        super();
        setOldBlockName("ExtraUtilities:chestFull");
        setOldTileEntityId("TileFullChest");
        setNewBlock(ModBlocks.MARGINALLY_MAXIMISED_CHEST);
    }

    @Override
    public BlockInfo doTileEntityTransformation(NBTTagCompound _oldTag, World world, Chunk chunk) {
        int meta = chunk
            .getBlockMetadata(_oldTag.getInteger("x") & 15, _oldTag.getInteger("y"), _oldTag.getInteger("z") & 15);
        // Don't ask me why our direction metas are so incomprehensible.
        meta = switch (meta) {
            case 1 -> 4;
            case 0 -> 3;
            case 3 -> 5;
            default -> 2;
        };
        return new BlockInfo(ModBlocks.MARGINALLY_MAXIMISED_CHEST.get(), meta, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT("TileEntityMarginallyMaximisedChestUIE", oldTag);
            tag.setTag("Items", oldTag.getTag("Items"));
            return tag;
        });
    }
}
