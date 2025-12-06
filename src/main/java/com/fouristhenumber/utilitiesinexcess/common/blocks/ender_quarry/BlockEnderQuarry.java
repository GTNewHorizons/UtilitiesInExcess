package com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderQuarry;

public class BlockEnderQuarry extends BlockContainer {

    public BlockEnderQuarry() {
        super(Material.iron);
        setBlockName("ender_quarry");
        setBlockTextureName("utilitiesinexcess:ender_quarry");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);

        int direction = (int) (((placer.rotationYaw + 45f) / 90f + 4f) % 4f);

        worldIn.setBlockMetadataWithNotify(x, y, z, direction, 2);

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderQuarry quarry) {
            quarry.facing = getFacing(direction);
            quarry.resetState();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (worldIn.isRemote) return true;
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderQuarry quarry) {
            if (player.isSneaking() && player.capabilities.isCreativeMode) {
                quarry.isCreativeBoosted = !quarry.isCreativeBoosted;
                player.addChatComponentMessage(new ChatComponentText((quarry.isCreativeBoosted ? "" : "Un-") + "Creative-Boosted Quarry."));
                return true;
            }
            if (quarry.state == TileEntityEnderQuarry.QuarryWorkState.STOPPED) {
                quarry.scanForWorkAreaFromMarkers(player);
                if (quarry.state == TileEntityEnderQuarry.QuarryWorkState.RUNNING) {
                    return true;
                }
            }
            player.addChatComponentMessage(new ChatComponentText(quarry.getState()));
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (!worldIn.isRemote && te instanceof TileEntityEnderQuarry quarry) {
            quarry.scanSidesForTEs();
        }
    }

    public static ForgeDirection getFacing(int meta) {
        return switch (meta) {
            case 0 -> ForgeDirection.SOUTH;
            case 1 -> ForgeDirection.WEST;
            case 2 -> ForgeDirection.NORTH;
            case 3 -> ForgeDirection.EAST;
            default -> ForgeDirection.UNKNOWN;
        };
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnderQuarry();
    }
}
