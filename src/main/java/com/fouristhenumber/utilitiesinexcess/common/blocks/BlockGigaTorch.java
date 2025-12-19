package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static net.minecraftforge.common.util.ForgeDirection.*;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityGigaTorch;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

public class BlockGigaTorch extends BlockContainer {

    public BlockGigaTorch() {
        super(Material.circuits);
        setBlockName("giga_torch");
        setBlockTextureName("utilitiesinexcess:giga_torch");
        setHardness(0.0F);
        setLightLevel(0.9375F); // 15 light level
        setStepSound(soundTypeWood);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityGigaTorch();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        if (World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)) {
            return true;
        } else {
            Block block = worldIn.getBlock(x, y - 1, z);
            return block.canPlaceTorchOnTop(worldIn, x, y - 1, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canPlaceBlockAt(worldIn, x, y, z) && worldIn.getBlock(x, y, z) == this) {
            this.dropBlockAsItem(worldIn, x, y, z, 0, 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    public static class ItemBlockGigaTorch extends ItemBlock {

        public ItemBlockGigaTorch(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                StatCollector.translateToLocalFormatted("tile.giga_torch.desc", BlockConfig.gigaTorch.gigaTorchRange));
        }
    }
}
