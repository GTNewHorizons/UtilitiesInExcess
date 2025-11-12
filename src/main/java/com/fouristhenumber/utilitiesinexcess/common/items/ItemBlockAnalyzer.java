package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemBlockAnalyzer extends Item {

    public ItemBlockAnalyzer() {
        super();
        this.setTextureName("utilitiesinexcess:block_analyzer");
        this.setUnlocalizedName("block_analyzer");
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        Block clicked = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        player.addChatMessage(new ChatComponentTranslation("chat.block_analyzer.header"));
        player
            .addChatMessage(new ChatComponentTranslation("chat.block_analyzer.blockid", clicked.getUnlocalizedName()));
        player.addChatMessage(new ChatComponentTranslation("chat.block_analyzer.blockmeta", meta));

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tile.writeToNBT(tag);
            player.addChatMessage(new ChatComponentTranslation("chat.block_analyzer.foundTE", tag.getString("id")));
            Stream<String> nbtKeys = tag.func_150296_c()
                .stream()
                .sorted();
            nbtKeys.forEach(
                s -> player.addChatMessage(
                    new ChatComponentTranslation(
                        "chat.block_analyzer.nbttag",
                        s,
                        tag.getTag(s)
                            .toString())));
        }
        player.addChatMessage(new ChatComponentTranslation("chat.block_analyzer.footer"));
        return true;
    }
}
