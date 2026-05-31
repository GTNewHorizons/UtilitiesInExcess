package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityFilterPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;


public class BlockFilterPipe extends BlockPipeBase
{
    public IIcon[] icons = new IIcon[6];

    public BlockFilterPipe() {
        super(Material.iron);
        this.setBlockName("filter_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFilterPipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (int i = 0; i < 6; i++)
        {
            icons[i] = reg.registerIcon("utilitiesinexcess:filter_pipe_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return icons[side];
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
                                    float subY, float subZ) {
        if (!worldIn.isRemote)
        {
            GuiFactories.tileEntity().open(player, x, y, z);
        }
        return true;
    }
}
