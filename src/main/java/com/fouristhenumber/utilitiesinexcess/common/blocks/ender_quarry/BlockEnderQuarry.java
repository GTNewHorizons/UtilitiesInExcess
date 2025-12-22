package com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
                player.addChatComponentMessage(
                    new ChatComponentText((quarry.isCreativeBoosted ? "" : "Un-") + "Creative-Boosted Quarry."));
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
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityEnderQuarry quarry) {
            dropContent(quarry, worldIn);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public void dropContent(TileEntityEnderQuarry quarry, World world) {
        for (ItemStack item : quarry.retrieveAllItems()) {
            float dx = world.rand.nextFloat() * 0.8F + 0.1F;
            float dy = world.rand.nextFloat() * 0.8F + 0.1F;
            float dz = world.rand.nextFloat() * 0.8F + 0.1F;
            float f3 = 0.05F;

            while (item.stackSize > 0) {
                int stackSize = Math.min(item.stackSize, 64);
                item.stackSize -= stackSize;

                EntityItem entityitem = new EntityItem(
                    world,
                    (float) quarry.xCoord + dx,
                    (float) quarry.yCoord + dy,
                    (float) quarry.zCoord + dz,
                    new ItemStack(item.getItem(), stackSize, item.getItemDamage()));

                entityitem.motionX = (float) world.rand.nextGaussian() * f3;
                entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) world.rand.nextGaussian() * f3;

                if (item.hasTagCompound()) {
                    entityitem.getEntityItem()
                        .setTagCompound(
                            (NBTTagCompound) item.getTagCompound()
                                .copy());
                }
                world.spawnEntityInWorld(entityitem);
            }
        }
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
