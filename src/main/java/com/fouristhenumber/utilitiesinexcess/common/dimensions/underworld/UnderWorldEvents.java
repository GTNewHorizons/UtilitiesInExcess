package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.world.WorldEvent;

import com.fouristhenumber.utilitiesinexcess.config.dimensions.UnderWorldConfig;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorPotionEffect;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PacketAggressiveMobSpawn;
import com.fouristhenumber.utilitiesinexcess.network.client.PacketUnderworldAttack;
import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

/// Various event hooks for the under world
public class UnderWorldEvents {

    public static final UnderWorldEvents INSTANCE = new UnderWorldEvents();

    public static void init() {
        MinecraftForge.TERRAIN_GEN_BUS.register(INSTANCE);
        MinecraftForge.ORE_GEN_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    /// Disable all plant life decoration
    @SubscribeEvent
    public void preventUnderworldDecoration(Decorate event) {
        if (event.world.provider instanceof WorldProviderUnderWorld) {
            event.setResult(Result.DENY);
        }
    }

    /// Disable dirt & gravel spawns, along with ore spawns if the config is disabled
    @SubscribeEvent
    public void preventDirtBlobs(GenerateMinable event) {
        if (event.world.provider instanceof WorldProviderUnderWorld) {
            switch (event.type) {
                case DIRT, GRAVEL -> {
                    event.setResult(Result.DENY);
                }
                case CUSTOM -> {
                    if (!UnderWorldConfig.spawnCustomOre) {
                        event.setResult(Result.DENY);
                    }
                }
                default -> {
                    if (!UnderWorldConfig.spawnVanillaOre) {
                        event.setResult(Result.DENY);
                    }
                }
            }
        }
    }

    /// Significantly reduce the duration of night vision for survival players
    @SubscribeEvent
    public void reduceNightVision(PlayerTickEvent event) {
        if (event.phase != Phase.END) return;
        if (event.player.capabilities.disableDamage) return;

        if (event.player.worldObj.provider instanceof WorldProviderUnderWorld) {
            PotionEffect effect = event.player.getActivePotionEffect(Potion.nightVision);

            if (effect != null && effect.getDuration() > 21 * 20) {
                int duration = effect.getDuration();

                ((AccessorPotionEffect) effect).setDuration(duration - 19);
            }
        }
    }

    private final Map<EntityPlayer, Integer> timeInDarkness = new MapMaker().weakKeys().makeMap();

    private static final DamageSource GHOST = new DamageSource("underworld-ghost");

    /// Damages survival players that sit in darkness (<3 light) for too long
    @SubscribeEvent
    public void damagePlayersInDark(PlayerTickEvent event) {
        if (event.phase != Phase.END) return;
        if (event.side != Side.SERVER) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.capabilities.disableDamage) return;

        if (player.worldObj.provider instanceof WorldProviderUnderWorld) {
            int light = player.worldObj.getBlockLightValue(
                MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ));

            if (light < 3) {
                int time = timeInDarkness.getOrDefault(player, 0);

                time++;

                timeInDarkness.put(player, time);

                // After 10 seconds, the player takes 4 ghost damage every 4 seconds.
                if (time > 10 * 20 && time % 80 == 0) {
                    event.player.attackEntityFrom(GHOST, 4);
                    // Creates the particles and plays the sound
                    PacketHandler.INSTANCE.sendTo(new PacketUnderworldAttack(), player);
                }
            }
        }
    }

    /// When difficulty is enabled, reduce mob spawn rates outside of dangerous zones
    @SubscribeEvent
    public void reduceSpawnRates(WorldEvent.PotentialSpawns event) {
        if (!UnderWorldConfig.enableDifficulty) return;

        int chunkX = event.x >> 4;
        int chunkZ = event.z >> 4;

        double difficulty = ((ChunkProviderUnderWorld) ((WorldServer) event.world).theChunkProviderServer.currentChunkProvider).getDifficulty(chunkX, chunkZ);

        if (event.world.rand.nextDouble() > difficulty) {
            event.setCanceled(true);
        }
    }

    enum Zone {
        Normal,
        Aggressive
    }

    private final Map<EntityPlayer, Zone> playerZone = new MapMaker().weakKeys().makeMap();

    /// Spawn extra mobs on top of the player when the player is in a difficult zone.
    /// Notifies the player when this is about to happen via a spooky chat message.
    @SubscribeEvent
    public void doAggressiveMobSpawning(PlayerTickEvent event) {
        if (event.phase != Phase.END) return;
        if (event.side != Side.SERVER) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        WorldServer world = (WorldServer) event.player.worldObj;

        // Only spawn once every 30 seconds
        if (world.getTotalWorldTime() % 600 != 0) return;

        if (!world.getGameRules().getGameRuleBooleanValue("doMobSpawning")) return;
        if (MinecraftServer.getServer().func_147135_j() == EnumDifficulty.PEACEFUL) return;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        double difficulty = ((ChunkProviderUnderWorld) world.theChunkProviderServer.currentChunkProvider).getDifficulty(chunkX, chunkZ);

        boolean isAggressive = difficulty >= 2;

        Zone current = isAggressive ? Zone.Aggressive : Zone.Normal;
        Zone prev = playerZone.put(player, current);

        if (prev != current) {
            if (isAggressive) {
                player.addChatComponentMessage(new ChatComponentTranslation("uie.chat.underworld_aggressive"));
            } else {
                if (prev != null) {
                    player.addChatComponentMessage(new ChatComponentTranslation("uie.chat.underworld_safe"));
                }
            }

            // Give the player some notice instead of spawning mobs on them immediately
            return;
        }

        if (!isAggressive) return;

        List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(x - 8, y - 8, z - 8, x + 8, y + 8, z + 8));

        // Limit the number of mobs surrounding the player depending on the difficulty
        if (mobs.size() > difficulty * 2 + 4) return;

        int count = Math.min(8, (int) (world.rand.nextDouble() * difficulty * 2) + 3);

        for (int i = 0; i < 20 && count > 0; i++) {
            // Pick a random block within ~8 blocks of the player
            // Mobs may spawn on top of the player if they aren't in light
            int dx = world.rand.nextInt(16) - 8;
            int dy = world.rand.nextInt(8) - 4;
            int dz = world.rand.nextInt(16) - 8;

            int x2 = dx + x;
            int y2 = dy + y;
            int z2 = dz + z;

            if (!world.isAirBlock(x2, y2, z2)) continue;

            if (!world.getBlock(x2, y2 - 1, z2).canCreatureSpawn(EnumCreatureType.monster, world, x2, y2 - 1, z2)) {
                continue;
            }

            if (world.getBlockLightValue(x2, y2, z2) > 7) continue;

            // Spawn a random monster
            var spawnEntry = world.spawnRandomCreature(EnumCreatureType.monster, x2, y2, z2);

            EntityLiving entity;

            try {
                entity = spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
            } catch (Exception exception) {
                exception.printStackTrace();
                continue;
            }

            entity.setLocationAndAngles(dx + x + 0.5, y2, dz + z + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);

            if (!entity.getCanSpawnHere()) continue;

            entity.onSpawnWithEgg(null);
            world.spawnEntityInWorld(entity);

            count--;

            // Send a packet that creates some particles and plays a sound
            PacketHandler.INSTANCE.sendToAllAround(new PacketAggressiveMobSpawn(x2, y2, z2), new TargetPoint(world.provider.dimensionId, x2, y2, z2, 64));
        }
    }
}
