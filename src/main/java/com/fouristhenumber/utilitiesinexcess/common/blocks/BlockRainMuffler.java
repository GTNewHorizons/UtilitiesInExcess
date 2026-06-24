package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler.NBT_RAIN_MUFFLED;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class BlockRainMuffler extends BlockContainer implements IWailaDataProvider {

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
        if (!worldIn.isRemote) {
            return true;
        }

        NBTTagCompound playerNBT = player.getEntityData();
        NBTTagCompound persistentNBT = playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        boolean nowActive = !persistentNBT.getBoolean(NBT_RAIN_MUFFLED);
        persistentNBT.setBoolean(NBT_RAIN_MUFFLED, nowActive);
        playerNBT.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentNBT);
        if (nowActive) {
            player.addChatMessage(new ChatComponentTranslation("tile.rain_muffler.chat.global_enable"));
        } else {
            player.addChatMessage(new ChatComponentTranslation("tile.rain_muffler.chat.global_disable"));
        }
        return true;
    }

    public static class ItemBlockRainMuffler extends ItemBlock {

        public ItemBlockRainMuffler(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted("tile.rain_muffler.desc.1", BlockConfig.rainMuffler.rainMufflerRange));
        }
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        NBTTagCompound playerNBT = player.getEntityData();
        NBTTagCompound persistentNBT = playerNBT.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (persistentNBT.getBoolean(NBT_RAIN_MUFFLED)) {
            currentTip.add(StatCollector.translateToLocal("tile.rain_muffler.waila.global_enabled"));
        } else {
            currentTip.add(StatCollector.translateToLocal("tile.rain_muffler.waila.global_disabled"));
        }

        return currentTip;
    }

    // Snubs
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return tag;
    }

    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }

    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }
}
