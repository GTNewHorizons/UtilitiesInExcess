package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.UIETeleporter;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.UnderWorldSourceProperty;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.WorldProviderUnderWorld;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.UnderWorldConfig;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortalUnderWorld extends BlockContainer {

    public BlockPortalUnderWorld() {
        super(Material.rock);

        setBlockName("underworld_portal");
        setResistance(60000000);
        setLightOpacity(0);
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ISBRHUnderworldPortal.RENDER_ID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return ModBlocks.BEDROCKIUM_BLOCK.get()
            .getIcon(side, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPortalUnderWorld();
    }

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityPortalUnderWorld portal && portal.invulnerable) {
            return -1f;
        } else {
            return 5f;
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);

        NBTTagCompound tag = itemIn.getTagCompound();

        if (tag != null && worldIn.getTileEntity(x, y, z) instanceof TileEntityPortalUnderWorld tile) {
            tile.hasDest = true;
            tile.destX = tag.getInteger("destX");
            tile.destY = tag.getInteger("destY");
            tile.destZ = tag.getInteger("destZ");
        }
    }

    private TileEntityPortalUnderWorld tile;

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        super.onBlockHarvested(worldIn, x, y, z, meta, player);

        tile = (TileEntityPortalUnderWorld) worldIn.getTileEntity(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("destX", tile.destX);
        tag.setInteger("destY", tile.destY);
        tag.setInteger("destZ", tile.destZ);

        tile = null;

        ItemStack stack = new ItemStack(this, 1);
        stack.setTagCompound(tag);

        ArrayList<ItemStack> list = new ArrayList<>();

        list.add(stack);

        return list;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {

        if (!world.isRemote) {
            if (world.provider instanceof WorldProviderUnderWorld) {
                UnderWorldSourceProperty source = (UnderWorldSourceProperty) player
                    .getExtendedProperties(UnderWorldSourceProperty.PROP_KEY);

                WorldServer dest = MinecraftServer.getServer()
                    .worldServerForDimension(source.entranceWorld);

                BlockPos spawn = findSpawnLocation(dest, source.entranceX, source.entranceY, source.entranceZ);

                if (spawn == null) {
                    player.addChatComponentMessage(new ChatComponentTranslation("uie.chat.portal_blocked"));
                } else {
                    teleport((EntityPlayerMP) player, dest, spawn.x, spawn.y, spawn.z);
                }
            } else {
                if (world.getTileEntity(x, y, z) instanceof TileEntityPortalUnderWorld tile) {
                    WorldServer dest = MinecraftServer.getServer()
                        .worldServerForDimension(UnderWorldConfig.underWorldDimensionId);

                    if (!tile.hasDest || dest.getBlock(tile.destX, tile.destY, tile.destZ) != this) {
                        BlockPos existing = findPortal(dest, x, z);

                        if (existing != null) {
                            tile.hasDest = true;
                            tile.destX = existing.x;
                            tile.destY = existing.y;
                            tile.destZ = existing.z;
                        } else {
                            generateSpawnRoom(dest, x, 150, z);

                            tile.hasDest = true;
                            tile.destX = x;
                            tile.destY = 150;
                            tile.destZ = z;
                        }
                    }

                    BlockPos spawn = findPortal(dest, tile.destX, tile.destZ);

                    if (spawn == null) {
                        player.addChatComponentMessage(new ChatComponentTranslation("uie.chat.portal_blocked"));
                    } else {
                        UnderWorldSourceProperty source = (UnderWorldSourceProperty) player
                            .getExtendedProperties(UnderWorldSourceProperty.PROP_KEY);

                        source.entranceWorld = world.provider.dimensionId;
                        source.entranceX = x;
                        source.entranceY = y;
                        source.entranceZ = z;

                        teleport((EntityPlayerMP) player, dest, spawn.x, spawn.y, spawn.z);
                    }
                }
            }
        }

        return true;
    }

    private BlockPos findSpawnLocation(World world, int x, int y, int z) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir == ForgeDirection.DOWN) continue;

            if (!world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) continue;
            if (!world.isAirBlock(x + dir.offsetX, y + dir.offsetY + 1, z + dir.offsetZ)) continue;

            return new BlockPos(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
        }

        return null;
    }

    private void teleport(EntityPlayerMP player, WorldServer world, int x, int y, int z) {
        UIETeleporter teleporter = new UIETeleporter(world, x, y, z);

        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getConfigurationManager()
            .transferPlayerToDimension(player, world.provider.dimensionId, teleporter);
    }

    private BlockPos findPortal(World world, int x, int z) {
        for (int y = 0; y < world.getActualHeight(); y++) {
            for (int dz = -16; dz <= 16; dz++) {
                for (int dx = -16; dx <= 16; dx++) {
                    if (world.getBlock(x + dx, y, z + dz) == this) {
                        return new BlockPos(x + dx, y, z + dz);
                    }
                }
            }
        }

        return null;
    }

    private void generateSpawnRoom(World world, int x, int y, int z) {
        for (int dy = -1; dy <= 4; dy++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dx = -3; dx <= 3; dx++) {
                    boolean surface = dx == -3 || dx == 3 || dy == -1 || dy == 4 || dz == -3 || dz == 3;

                    if (surface) {
                        world.setBlock(x + dx, y + dy, z + dz, Blocks.cobblestone);
                    } else {
                        world.setBlock(x + dx, y + dy, z + dz, Blocks.air);
                    }
                }
            }
        }

        world.setBlock(x, y, z, this);
        ((TileEntityPortalUnderWorld) world.getTileEntity(x, y, z)).invulnerable = true;

        world.setBlock(x - 2, y, z - 2, Blocks.torch);
        world.setBlock(x + 2, y, z - 2, Blocks.torch);
        world.setBlock(x + 2, y, z + 2, Blocks.torch);
        world.setBlock(x - 2, y, z + 2, Blocks.torch);

        world.setBlock(x + 2, y, z, Blocks.torch);
        world.setBlock(x - 2, y, z, Blocks.torch);
        world.setBlock(x, y, z + 2, Blocks.torch);
        world.setBlock(x, y, z - 2, Blocks.torch);
    }
}
