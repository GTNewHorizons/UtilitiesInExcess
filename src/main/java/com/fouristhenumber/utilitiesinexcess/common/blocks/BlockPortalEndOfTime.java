package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.UIETeleporter;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.DimensionPortalData;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.EndOfTimeSourceProperty;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.WorldProviderEndOfTime;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.EndOfTimeConfig;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

public class BlockPortalEndOfTime extends Block {

    public BlockPortalEndOfTime() {
        super(Material.rock);

        setBlockName("temporal_gate");
        setBlockTextureName("utilitiesinexcess:temporal_gate");
        setResistance(5);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        if (!world.isRemote && world.provider instanceof WorldProviderEndOfTime) {
            DimensionPortalData.get(world)
                .setTarget(x, y, z);
        }
        return meta;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (world.isRemote) {
            return true;
        }
        if (world.provider instanceof WorldProviderEndOfTime) {
            if (player.isSneaking()) {
                player.travelToDimension(0);
                ChunkCoordinates spawn = player.worldObj.getSpawnPoint();
                int topY = player.worldObj.getTopSolidOrLiquidBlock(x, z);
                player.setPositionAndUpdate(spawn.posX + 0.5, topY, spawn.posZ + 0.5);
                return true;
            }
            EndOfTimeSourceProperty source = (EndOfTimeSourceProperty) player
                .getExtendedProperties(EndOfTimeSourceProperty.PROP_KEY);
            WorldServer dest = MinecraftServer.getServer()
                .worldServerForDimension(source.entranceWorld);
            BlockPos spawn = findSpawnLocation(dest, source.entranceX, source.entranceY, source.entranceZ);
            if (spawn == null) {
                player.addChatComponentMessage(new ChatComponentTranslation("uie.chat.portal_blocked"));
            } else {
                teleport((EntityPlayerMP) player, dest, spawn.x, spawn.y, spawn.z);
            }
        } else {
            WorldServer dest = MinecraftServer.getServer()
                .worldServerForDimension(EndOfTimeConfig.endOfTimeDimensionId);
            if (dest.getBlock(0, 64, 0) != Blocks.bedrock) {
                generateSpawnArea(dest, 0, 65, 0);
            }
            ChunkCoordinates spawn = DimensionPortalData.get(dest)
                .getTarget();
            if (spawn.posY <= 0 || dest.getBlock(spawn.posX, spawn.posY, spawn.posZ) != this) {
                spawn = new ChunkCoordinates(0, 65, 0);
            }
            EndOfTimeSourceProperty source = (EndOfTimeSourceProperty) player
                .getExtendedProperties(EndOfTimeSourceProperty.PROP_KEY);
            source.entranceWorld = world.provider.dimensionId;
            source.entranceX = x;
            source.entranceY = y;
            source.entranceZ = z;
            teleport((EntityPlayerMP) player, dest, spawn.posX, spawn.posY + 1, spawn.posZ);
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

    private void generateSpawnArea(WorldServer world, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        for (int cx = -1; cx <= 1; cx++) {
            for (int cz = -1; cz <= 1; cz++) {
                world.theChunkProviderServer.loadChunk(chunkX + cx, chunkZ + cz);
            }
        }

        buildPlatform(world, x, y, z);
        buildPlatform(world, x + 12, y - 1, z);
        buildBridge(world, x + 4, y - 1, z);
        buildDock(world, x + 17, y - 1, z);
        decorate(world, x, y, z);
        DimensionPortalData.get(world)
            .setTarget(x, y, z);
    }

    public static void buildPlatform(World world, int x, int y, int z) {
        int baseSize = 9;
        int halfBase = baseSize / 2;

        // Build upside down pyramid layers
        for (int layer = 0; layer < halfBase + 1; layer++) {
            int size = baseSize - (layer * 2);
            int yLevel = y - layer;

            for (int dx = -size / 2; dx <= size / 2; dx++) {
                for (int dz = -size / 2; dz <= size / 2; dz++) {
                    world.setBlock(x + dx, yLevel, z + dz, Blocks.stonebrick, 0, 2);
                }
            }

            // Stair ring
            if (layer != 0) {
                int prevSize = size + 2;
                int edge = prevSize / 2;
                // North
                for (int dx = -edge; dx <= edge - 1; dx++) {
                    world.setBlock(x + dx, yLevel, z - edge, Blocks.stone_brick_stairs, 6, 2);
                }
                // East
                for (int dz = -edge + 1; dz <= edge; dz++) {
                    world.setBlock(x - edge, yLevel, z + dz, Blocks.stone_brick_stairs, 4, 2);
                }
                // West
                for (int dz = -edge; dz <= edge - 1; dz++) {
                    world.setBlock(x + edge, yLevel, z + dz, Blocks.stone_brick_stairs, 5, 2);
                }
                // South
                for (int dx = -edge + 1; dx <= edge; dx++) {
                    world.setBlock(x + dx, yLevel, z + edge, Blocks.stone_brick_stairs, 7, 2);
                }
            }
        }

        int tipY = y - (halfBase + 1);
        if (ModBlocks.LAPIS_AETHERIUS.isEnabled()) {
            world.setBlock(x, tipY, z, ModBlocks.LAPIS_AETHERIUS.get(), 14, 2);
        } else {
            world.setBlock(x, tipY, z, Blocks.stonebrick, 0, 2);
        }

        // Fence ring
        // North
        int topEdge = baseSize / 2;
        for (int dx = -topEdge; dx <= topEdge; dx++) {
            world.setBlock(x + dx, y + 1, z - topEdge, Blocks.fence, 0, 2);
        }
        // East
        for (int dz = -topEdge; dz <= topEdge; dz++) {
            world.setBlock(x - topEdge, y + 1, z + dz, Blocks.fence, 0, 2);
        }
        // West
        for (int dz = -topEdge; dz <= topEdge; dz++) {
            world.setBlock(x + topEdge, y + 1, z + dz, Blocks.fence, 0, 2);
        }
        // South
        for (int dx = -topEdge; dx <= topEdge; dx++) {
            world.setBlock(x + dx, y + 1, z + topEdge, Blocks.fence, 0, 2);
        }
    }

    private void buildBridge(World world, int x, int y, int z) {
        for (int dx = 0; dx < 4; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlock(x + dx, y, z + dz, Blocks.stonebrick, 0, 2);
            }
        }

        world.setBlock(x, y + 2, z, Blocks.air, 0, 2);
        world.setBlock(x, y + 1, z, Blocks.stone_brick_stairs, 1, 2);

        for (int dx = 1; dx <= 3; dx++) {
            for (int dz : new int[] { -1, 1 }) {
                world.setBlock(x + dx, y + 1, z + dz, Blocks.fence, 0, 2);
                if (dx == 1) {
                    world.setBlock(x + dx, y + 2, z + dz, Blocks.fence, 0, 2);
                }
            }
        }

        world.setBlock(x + 4, y + 1, z, Blocks.air, 0, 2);
    }

    private void buildDock(World world, int x, int y, int z) {
        for (int dz = -1; dz <= 1; dz++) {
            world.setBlock(x, y, z + dz, Blocks.stone_stairs, 5, 2);
        }
        for (int dz = -1; dz <= 1; dz++) {
            world.setBlock(x + 1, y, z + dz, Blocks.stone_slab, 11, 2);
        }
        world.setBlock(x + 2, y, z - 1, Blocks.stone_stairs, 4, 2);
        world.setBlock(x + 2, y, z, Blocks.stone_stairs, 1, 2);
        world.setBlock(x + 2, y, z + 1, Blocks.stone_stairs, 4, 2);
        for (int dx = 0; dx <= 2; dx++) {
            world.setBlock(x + dx, y + 1, z - 1, Blocks.cobblestone_wall, 0, 2);
            world.setBlock(x + dx, y + 1, z + 1, Blocks.cobblestone_wall, 0, 2);
        }
        world.setBlock(x - 1, y + 1, z, Blocks.air, 0, 2);
    }

    private void decorate(World world, int x, int y, int z) {
        world.setBlock(x, y, z, this, 0, 2);
        world.setBlock(x, y + 1, z, Blocks.air, 0, 2);
        world.setBlock(x, y + 2, z, Blocks.air, 0, 2);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlock(x + dx, y - 1, z + dz, Blocks.bedrock, 0, 2);
            }
        }
        for (int dy = 0; dy <= 3; dy++) {
            world.setBlock(x + 12, y + dy, z, Blocks.cobblestone_wall, 0, 2);
        }

        world.setBlock(x + 12, y + 4, z, Blocks.glowstone, 0, 2);
        world.setBlock(x + 12, y + 5, z, Blocks.stone_slab, 3, 2);
        world.setBlock(x + 15, y, z - 3, Blocks.cauldron, 3, 2);
        world.setBlock(x + 15, y, z - 2, Blocks.flower_pot, 0, 2);
        TileEntity tileEntity = world.getTileEntity(x + 15, y, z - 2);
        if (tileEntity instanceof TileEntityFlowerPot pot) {
            pot.func_145964_a((Item) Item.itemRegistry.getObject("sapling"), 0);
        }
        world.setBlock(x + 9, y, z + 3, Blocks.flower_pot, 0, 2);
        tileEntity = world.getTileEntity(x + 9, y, z + 3);
        if (tileEntity instanceof TileEntityFlowerPot pot) {
            pot.func_145964_a((Item) Item.itemRegistry.getObject("sapling"), 0);
        }
    }

    public static class ItemBlockPortalEndOfTime extends ItemBlock {

        public ItemBlockPortalEndOfTime(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tile.temporal_gate.desc"));
        }
    }
}
