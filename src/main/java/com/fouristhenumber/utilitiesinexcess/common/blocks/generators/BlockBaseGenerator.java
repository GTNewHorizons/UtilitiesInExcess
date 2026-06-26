package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.dropItemsFromIInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityBaseGenerator;

public abstract class BlockBaseGenerator extends BlockContainer {

    public int multiplier;

    protected BlockBaseGenerator(String id, int mult) {
        super(Material.iron);
        multiplier = mult;
        setHardness(5F);
        setBlockName(id);
        setBlockTextureName("utilitiesinexcess:generators/" + id);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            GuiFactories.tileEntity()
                .open(playerIn, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof IInventory inv) {
            dropItemsFromIInventory(0, inv, worldIn, te.xCoord, te.yCoord, te.zCoord);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityBaseGenerator generator) {
            generator.onNeighborBlockChange();
        }
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }
}
