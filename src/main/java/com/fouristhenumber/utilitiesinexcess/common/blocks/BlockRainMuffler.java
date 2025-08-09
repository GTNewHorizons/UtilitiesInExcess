package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler.NBT_RAIN_MUFFLED;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

public class BlockRainMuffler extends BlockContainer {

    public BlockRainMuffler() {
        super(Material.sponge);
        setStepSound(soundTypeCloth);
        setBlockName("rain_muffler");
        setBlockTextureName("utilitiesinexcess:rain_muffler");
        setHardness(0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRainMuffler();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityRainMuffler muffler) {
            muffler.onInputChanged();
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityRainMuffler muffler) {
            muffler.onInputChanged();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        NBTTagCompound playerNBT = player.getEntityData();
        NBTTagCompound persistentNBT = playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        boolean nowActive = !persistentNBT.getBoolean(NBT_RAIN_MUFFLED);
        persistentNBT.setBoolean(NBT_RAIN_MUFFLED, nowActive);
        playerNBT.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNBT);
        if (worldIn.isRemote) {
            if (nowActive) {
                player.addChatMessage(new ChatComponentTranslation("tile.rain_muffler.chat.global_enable"));
            } else {
                player.addChatMessage(new ChatComponentTranslation("tile.rain_muffler.chat.global_disable"));
            }
        }
        return true;
    }

    public static class ItemBlockRainMuffler extends ItemBlock {

        public ItemBlockRainMuffler(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            String formatted = StatCollector
                .translateToLocalFormatted("tile.rain_muffler.desc.1", BlockConfig.rainMuffler.rainMufflerRange);
            tooltip.add(formatted);
            tooltip.add(StatCollector.translateToLocal("tile.rain_muffler.desc.2"));
            tooltip.add(StatCollector.translateToLocal("tile.rain_muffler.desc.3"));
        }
    }
}
