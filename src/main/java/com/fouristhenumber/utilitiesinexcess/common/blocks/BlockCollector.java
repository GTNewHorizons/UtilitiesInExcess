package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;

public class BlockCollector extends BlockContainer {

    public BlockCollector() {
        super(Material.rock);
        setBlockName("collector");
        setBlockTextureName("utilitiesinexcess:collector");
        setHardness(1.5f);
        setResistance(1.5f);
    }

    // So you can open chests under it
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCollector();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        TileEntity tile = worldIn.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityCollector collector)) {
            return true;
        }

        collector.incrementSize(player);
        if (!worldIn.isRemote)
            player.addChatMessage(new ChatComponentTranslation("uie.chat.collector_size", collector.getSize()));
        collector.showBorderFor(40);
        worldIn.markBlockForUpdate(x, y, z);
        return true;
    }
}
