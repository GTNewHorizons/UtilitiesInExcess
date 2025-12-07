package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class MiniChestTransformation extends AbstractTileEntityTransformation {

    public MiniChestTransformation() {
        super();
        setOldBlockName("ExtraUtilities:chestMini");
        setOldTileEntityId("TileMiniChest");
        setNewBlock(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST);
    }

    @Override
    public BlockInfo doTileEntityTransformation(NBTTagCompound _oldTag, World world, Chunk chunk) {
        int meta = chunk
            .getBlockMetadata(_oldTag.getInteger("x") & 15, _oldTag.getInteger("y"), _oldTag.getInteger("z") & 15);
        // Don't ask me why our direction metas are so incomprehensible.
        meta = switch (meta) {
            case 3 -> 4;
            case 2 -> 3;
            case 1 -> 5;
            default -> 2;
        };
        return new BlockInfo(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST.get(), meta, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT("TileEntitySignificantlyShrunkChestUIE", oldTag);
            tag.setTag("Items", oldTag.getTag("Items"));
            return tag;
        });
    }
}
