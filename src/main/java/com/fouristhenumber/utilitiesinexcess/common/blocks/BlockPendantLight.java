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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPendantLight;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

public class BlockPendantLight extends BlockContainer {

    public BlockPendantLight() {
        super(Material.circuits);
        setBlockName("pendant_light");
        setBlockTextureName("utilitiesinexcess:pendant_light");
        setHardness(0.0F);
        setLightLevel(0.9375F); // 15 light level
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPendantLight();
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
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return worldIn.isSideSolid(x, y + 1, z, DOWN);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canPlaceBlockAt(worldIn, x, y, z) && worldIn.getBlock(x, y, z) == this) {
            this.dropBlockAsItem(worldIn, x, y, z, 0, 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    public static class ItemBlockPendantLight extends ItemBlock {

        public ItemBlockPendantLight(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted("tile.pendant_light.desc", BlockConfig.pendantLight.pendantLightRange));
        }
    }
}
