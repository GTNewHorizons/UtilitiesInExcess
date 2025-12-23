package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class SpikeTransformation implements IPosteaTransformation {

    @Override
    public void registerTransformations() {
        TileEntityReplacementManager
            .tileEntityTransformer("TileEntityEnchantedSpike", this::doTileEntityTransformation);
    }

    public BlockInfo doTileEntityTransformation(NBTTagCompound _oldTag, World world, Chunk chunk) {
        Block block = chunk
            .getBlock(_oldTag.getInteger("x") & 15, _oldTag.getInteger("y"), _oldTag.getInteger("z") & 15);

        return new BlockInfo(block, 0, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT("utilitiesinexcess:TileEntitySpike", oldTag);

            NBTTagCompound fakeWeapon = new NBTTagCompound();

            ItemStack fakeWeaponStack = new ItemStack(Item.getItemFromBlock(block));
            NBTTagCompound tagtag = (NBTTagCompound) oldTag.copy();
            tagtag.removeTag("x");
            tagtag.removeTag("y");
            tagtag.removeTag("z");
            tagtag.removeTag("id");
            fakeWeaponStack.setTagCompound(tagtag);
            fakeWeaponStack.writeToNBT(fakeWeapon);

            tag.setTag("FakeWeapon", fakeWeapon);

            String spikeType = ((BlockSpike) block).getSpikeType()
                .name();
            tag.setString("SpikeType", spikeType);

            return tag;
        });
    }
}
