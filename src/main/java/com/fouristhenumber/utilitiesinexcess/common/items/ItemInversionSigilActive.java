package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig.awakenedInversionDurability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import org.jetbrains.annotations.NotNull;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.entities.EntitySiegeProperty;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ItemInversionSigilActive extends Item {

    private static final String DURABILITY_NBT_KEY = "RemainingUses";
    private static final int BEACON_SEARCH_RADIUS = 6;
    private final int[][] BOLT_POSITIONS = { { 0, 0 }, { -5, 0 }, { 5, 0 }, { 0, -5 }, { 0, 5 } };
    private final String PROP_KEY = "entity-siege";
    private World beaconSpawnWorld = DimensionManager.getWorld(1);

    private EntitySiegeProperty getProperties(EntityPlayer player) {
        return (EntitySiegeProperty) player.getExtendedProperties(PROP_KEY);
    }

    private List<EntityPlayer> getSiegePlayers() {
        List<EntityPlayer> siegePlayers = new ArrayList<>();
        for (Entity curentity : beaconSpawnWorld.loadedEntityList) {
            if (curentity instanceof EntityPlayer player
                && curentity.getExtendedProperties(PROP_KEY) instanceof EntitySiegeProperty) {
                if (getProperties(player).siege) {
                    siegePlayers.add(player);
                }
            }
        }
        return siegePlayers;
    }

    private boolean checkSpiral(World world, int x, int y, int z) {
        int[][] BASE = { { 0, -1 }, { 1, -1 }, { 2, -1 }, { 2, 0 }, { 2, 1 }, { 2, 2 }, { 2, 3 }, { 1, 3 }, { 0, 3 },
            { -1, 3 }, { -2, 3 }, { -3, 3 }, { -4, 3 }, { -4, 2 }, { -4, 1 }, { -4, 0 }, { -4, -1 }, { -4, -2 },
            { -4, -3 }, { -4, -4 } };
        int[][] REDSTONE_SPOTS = new int[40][2];
        int[][] STRING_SPOTS = new int[40][2];
        int[][] BASE_ALTERNATE = new int[20][2];
        int[][] REDSTONE_SPOTS_ALTERNATE = new int[40][2];
        int[][] STRING_SPOTS_ALTERNATE = new int[40][2];
        for (int i = 0; i < 20; i++) {
            REDSTONE_SPOTS[2 * i] = BASE[i];
            REDSTONE_SPOTS[2 * i + 1][0] = -1 * BASE[i][0];
            REDSTONE_SPOTS[2 * i + 1][1] = -1 * BASE[i][1];
            STRING_SPOTS[2 * i][0] = -1 * BASE[i][1];
            STRING_SPOTS[2 * i][1] = BASE[i][0];
            STRING_SPOTS[2 * i + 1][0] = BASE[i][1];
            STRING_SPOTS[2 * i + 1][1] = -1 * BASE[i][0];
        }
        for (int i = 0; i < 20; i++) {
            BASE_ALTERNATE[i][0] = -1 * BASE[i][1];
            BASE_ALTERNATE[i][1] = BASE[i][0];
            REDSTONE_SPOTS_ALTERNATE[2 * i] = BASE_ALTERNATE[i];
            REDSTONE_SPOTS_ALTERNATE[2 * i + 1][0] = -1 * BASE_ALTERNATE[i][0];
            REDSTONE_SPOTS_ALTERNATE[2 * i + 1][1] = -1 * BASE_ALTERNATE[i][1];
            STRING_SPOTS_ALTERNATE[2 * i][0] = -1 * BASE_ALTERNATE[i][1];
            STRING_SPOTS_ALTERNATE[2 * i][1] = BASE_ALTERNATE[i][0];
            STRING_SPOTS_ALTERNATE[2 * i + 1][0] = BASE_ALTERNATE[i][1];
            STRING_SPOTS_ALTERNATE[2 * i + 1][1] = -1 * BASE_ALTERNATE[i][0];
        }
        boolean baseTrue = true, alternateTrue = true;
        for (int i = 0; i < 40; i++) {
            if (world.getBlock(x + REDSTONE_SPOTS[i][0], y, z + REDSTONE_SPOTS[i][1]) != Blocks.redstone_wire) {
                baseTrue = false;
                break;
            }
            if (world.getBlock(x + STRING_SPOTS[i][0], y, z + STRING_SPOTS[i][1]) != Blocks.tripwire) {
                baseTrue = false;
                break;
            }
        }
        for (int i = 0; i < 40; i++) {
            if (world.getBlock(x + REDSTONE_SPOTS_ALTERNATE[i][0], y, z + REDSTONE_SPOTS_ALTERNATE[i][1])
                != Blocks.redstone_wire) {
                alternateTrue = false;
                break;
            }
            if (world.getBlock(x + STRING_SPOTS_ALTERNATE[i][0], y, z + STRING_SPOTS_ALTERNATE[i][1])
                != Blocks.tripwire) {
                alternateTrue = false;
                break;
            }
        }
        return baseTrue || alternateTrue;
    }

    private void siegeStart(World world, int beaconX, int beaconY, int beaconZ, EntityPlayer player) {
        EntitySiegeProperty source = getProperties(player);
        source.beaconSpawnX = beaconX;
        source.beaconSpawnY = beaconY;
        source.beaconSpawnZ = beaconZ;
        source.siege = true;
        source.siegeMobsKilled = 0;
        source.siegeTimer = 0;
        for (Entity curentity : beaconSpawnWorld.loadedEntityList) {
            if (curentity instanceof EntityEnderman) {
                curentity.setDead();
            }
        }
        for (int i = 0; i < 5; i++) {
            EntityLightningBolt bolt = new EntityLightningBolt(
                world,
                beaconX + BOLT_POSITIONS[i][0] + 0.5,
                beaconY + 0.5,
                beaconZ + BOLT_POSITIONS[i][1] + 0.5);
            world.addWeatherEffect(bolt);
            world.setBlock(beaconX + BOLT_POSITIONS[i][0], beaconY, beaconZ + BOLT_POSITIONS[i][1], Blocks.air);
        }
    }

    private void siegeEnd(boolean Won, EntityPlayer player) {
        EntitySiegeProperty source = getProperties(player);
        source.siege = false;
        source.siegeMobsKilled = 0;
        source.siegeTimer = 0;
        for (Entity curentity : beaconSpawnWorld.loadedEntityList) {
            if (curentity instanceof EntityMob) {
                curentity.setDead();
            }
        }
        if (Won) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack is = player.inventory.getStackInSlot(i);
                if (is != null && is.getItem() == this) {
                    player.inventory
                        .setInventorySlotContents(i, new ItemStack(ModItems.PSEUDO_INVERSION_SIGIL.get(), 1));
                    break;
                }
            }
        }
    }

    private boolean checkChest(TileEntityChest chest, ItemStack[] CHECKED_ITEMS, int ITEM_REQUIREMENT) {
        int CHECKED_ITEMS_SIZE = CHECKED_ITEMS.length;
        int requiredItemsAmount = 0;
        boolean[] hasItem = new boolean[CHECKED_ITEMS_SIZE];
        for (int i = 0; i < chest.getSizeInventory(); i++) {
            ItemStack stack = chest.getStackInSlot(i);
            for (int j = 0; j < CHECKED_ITEMS_SIZE; j++) {
                if (stack != null && ItemStack.areItemStacksEqual(stack, CHECKED_ITEMS[j])) {
                    hasItem[j] = true;
                    break;
                }
            }
        }
        for (int i = 0; i < CHECKED_ITEMS_SIZE; i++) {
            if (hasItem[i]) {
                requiredItemsAmount++;
            }
        }
        return requiredItemsAmount >= ITEM_REQUIREMENT;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            return false;
        }

        Block clicked = world.getBlock(x, y, z);
        if (clicked != Blocks.beacon) {
            return false;
        }

        if (world.isRemote) return true;

        boolean dimensionOk = (world.provider.dimensionId == 1);
        boolean chestNorthContentsOk;
        boolean chestEastContentsOk;
        boolean chestSouthContentsOk;
        boolean chestWestContentsOk;
        boolean spiralOk = checkSpiral(world, x, y, z);

        ItemStack[] CHEST_NORTH_CONTENTS = { new ItemStack(Blocks.stone), new ItemStack(Items.brick),
            new ItemStack(Blocks.glass), new ItemStack(Items.cooked_fished), new ItemStack(Blocks.hardened_clay),
            new ItemStack(Items.dye, 1, 2), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.cooked_beef),
            new ItemStack(Items.iron_ingot), new ItemStack(Items.cooked_chicken), new ItemStack(Items.gold_ingot),
            new ItemStack(Items.baked_potato), new ItemStack(Items.cooked_porkchop), new ItemStack(Items.netherbrick) };

        int[] POTION_IDS = { 8193, 8194, 8195, 8196, 8197, 8198, 8200, 8201, 8202, 8204, 8205, 8206, 8225, 8226, 8228,
            8229, 8232, 8233, 8234, 8236, 8257, 8258, 8259, 8260, 8262, 8264, 8265, 8267, 8268, 8269, 8270 };
        ItemStack[] CHEST_EAST_CONTENTS = new ItemStack[62];
        for (int i = 0; i < 31; i++) {
            CHEST_EAST_CONTENTS[2 * i] = new ItemStack(Items.potionitem, 1, POTION_IDS[i]);
            CHEST_EAST_CONTENTS[2 * i + 1] = new ItemStack(Items.potionitem, 1, POTION_IDS[i] + 8192);
        }

        ItemStack[] CHEST_SOUTH_CONTENTS = { new ItemStack(Blocks.grass), new ItemStack(Blocks.lapis_ore),
            new ItemStack(Blocks.dirt), new ItemStack(Blocks.obsidian), new ItemStack(Blocks.sand),
            new ItemStack(Blocks.diamond_ore), new ItemStack(Blocks.gravel), new ItemStack(Blocks.redstone_ore),
            new ItemStack(Blocks.gold_ore), new ItemStack(Blocks.clay), new ItemStack(Blocks.iron_ore),
            new ItemStack(Blocks.emerald_ore), new ItemStack(Blocks.coal_ore) };

        ItemStack[] CHEST_WEST_CONTENTS = { new ItemStack(Items.record_13), new ItemStack(Items.record_mellohi),
            new ItemStack(Items.record_cat), new ItemStack(Items.record_stal), new ItemStack(Items.record_blocks),
            new ItemStack(Items.record_strad), new ItemStack(Items.record_chirp), new ItemStack(Items.record_ward),
            new ItemStack(Items.record_far), new ItemStack(Items.record_11), new ItemStack(Items.record_mall),
            new ItemStack(Items.record_wait) };

        if (world.getTileEntity(x, y, z - 5) instanceof TileEntityChest chest) {
            chestNorthContentsOk = checkChest(chest, CHEST_NORTH_CONTENTS, InversionConfig.northChestRequiredItems);
        } else {
            chestNorthContentsOk = false;
        }
        if (world.getTileEntity(x + 5, y, z) instanceof TileEntityChest chest) {
            chestEastContentsOk = checkChest(chest, CHEST_EAST_CONTENTS, InversionConfig.eastChestRequiredItems);
        } else {
            chestEastContentsOk = false;
        }
        if (world.getTileEntity(x, y, z + 5) instanceof TileEntityChest chest) {
            chestSouthContentsOk = checkChest(chest, CHEST_SOUTH_CONTENTS, InversionConfig.southChestRequiredItems);
        } else {
            chestSouthContentsOk = false;
        }
        if (world.getTileEntity(x - 5, y, z) instanceof TileEntityChest chest) {
            chestWestContentsOk = checkChest(chest, CHEST_WEST_CONTENTS, InversionConfig.westChestRequiredItems);
        } else {
            chestWestContentsOk = false;
        }
        player.addChatMessage(
            new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.header")));
        if (dimensionOk) {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.dimension")));
        } else if (world.provider.dimensionId == 0) {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.overworld")));
        } else if (world.provider.dimensionId == -1) {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.nether")));
        } else {
            player.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("chat.pseudo_inversion_ritual.misc")));
        }
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestNorthContents",
                    (chestNorthContentsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestEastContents",
                    (chestEastContentsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestSouthContents",
                    (chestSouthContentsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted(
                    "chat.pseudo_inversion_ritual.chestWestContents",
                    (chestWestContentsOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector
                    .translateToLocalFormatted("chat.pseudo_inversion_ritual.spiral", (spiralOk ? "✓" : "✗"))));
        if (dimensionOk && chestNorthContentsOk && chestEastContentsOk && chestSouthContentsOk && chestWestContentsOk) {
            player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.ready"));
        }
        return true;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void whenServerTick(TickEvent.ServerTickEvent event) {
        if (beaconSpawnWorld == null) {
            beaconSpawnWorld = DimensionManager.getWorld(1);
        }
        List<EntityPlayer> playerList = getSiegePlayers();
        for (EntityPlayer player : playerList) {
            EntitySiegeProperty properties = getProperties(player);
            if (properties.siege) {
                properties.siegeTimer--;
            }
            if (properties.siegeTimer <= 0 && properties.siege) {
                properties.siegeTimer = 60 + beaconSpawnWorld.rand.nextInt(41);
                EntityMob entitymob = null;
                int mobType = beaconSpawnWorld.rand.nextInt(4);
                switch (mobType) {
                    case 0:
                        entitymob = new EntityZombie(beaconSpawnWorld);
                        break;
                    case 1:
                        entitymob = new EntitySkeleton(beaconSpawnWorld);
                        break;
                    case 2:
                        entitymob = new EntitySpider(beaconSpawnWorld);
                        break;
                    case 3:
                        entitymob = new EntityCreeper(beaconSpawnWorld);
                        break;
                }
                int offsetX = beaconSpawnWorld.rand.nextInt(11) - 5, offsetZ = beaconSpawnWorld.rand.nextInt(11) - 5;
                entitymob.setPosition(
                    properties.beaconSpawnX + offsetX,
                    properties.beaconSpawnY,
                    properties.beaconSpawnZ + offsetZ);
                beaconSpawnWorld.spawnEntityInWorld(entitymob);
                entitymob.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                    .setBaseValue(8.0D);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void whenPlayerLeavesEnd(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (getProperties(event.player).siege) {
            event.player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.leftEnd"));
            siegeEnd(false, event.player);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void whenPlayerLeavesEnd2(PlayerEvent.PlayerRespawnEvent event) {
        if (getProperties(event.player).siege) {
            event.player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.leftEnd"));
            siegeEnd(false, event.player);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void whenEndermanSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.entity instanceof EntityEnderman && (!getSiegePlayers().isEmpty())
            && event.world.provider.dimensionId == 1) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingDeath(LivingDeathEvent event) {

        World world = event.entityLiving.worldObj;

        if (!(event.source != null && event.source.getSourceOfDamage() instanceof EntityPlayer player)) {
            if (event.entityLiving instanceof EntityPlayer deadplayer && getProperties(deadplayer).siege) {
                deadplayer.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.death"));
                siegeEnd(false, deadplayer);
                return;
            }
            return;
        }
        EntitySiegeProperty source = getProperties(player);

        if (world.isRemote) return;

        if (event.entityLiving instanceof EntityMob && source.siege) {
            source.siegeMobsKilled++;
            if (source.siegeMobsKilled >= InversionConfig.SiegeRequiredMobsKill) {
                player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.victory"));
                siegeEnd(true, player);
            } else if (source.siegeMobsKilled == (int) (3 * InversionConfig.SiegeRequiredMobsKill) / 4) {
                player.addChatMessage(
                    new ChatComponentTranslation(
                        StatCollector.translateToLocalFormatted(
                            "chat.pseudo_inversion_ritual.threequarters",
                            source.siegeMobsKilled)));
            } else if (source.siegeMobsKilled == (int) (InversionConfig.SiegeRequiredMobsKill) / 2) {
                player.addChatMessage(
                    new ChatComponentTranslation(
                        StatCollector.translateToLocalFormatted(
                            "chat.pseudo_inversion_ritual.twoquarters",
                            source.siegeMobsKilled)));
            } else if (source.siegeMobsKilled == (int) (InversionConfig.SiegeRequiredMobsKill) / 4) {
                player.addChatMessage(
                    new ChatComponentTranslation(
                        StatCollector.translateToLocalFormatted(
                            "chat.pseudo_inversion_ritual.onequarter",
                            source.siegeMobsKilled)));
            }
            return;
        } else if (!(event.entityLiving instanceof EntityIronGolem)) {
            return;
        }

        if (!player.inventory.hasItem(this)) return;

        int radius = BEACON_SEARCH_RADIUS;
        int mobX = (int) Math.floor(event.entityLiving.posX);
        int mobY = (int) Math.floor(event.entityLiving.posY);
        int mobZ = (int) Math.floor(event.entityLiving.posZ);

        int beaconX = 0, beaconY = 0, beaconZ = 0;
        boolean found = false;

        for (int dx = -radius; dx <= radius && !found; dx++) {
            for (int dy = -2; dy <= 2 && !found; dy++) {
                for (int dz = -radius; dz <= radius && !found; dz++) {
                    int bx = mobX + dx;
                    int by = mobY + dy;
                    int bz = mobZ + dz;
                    if (world.getBlock(bx, by, bz) == Blocks.beacon) {
                        beaconX = bx;
                        beaconY = by;
                        beaconZ = bz;
                        found = true;
                    }
                }
            }
        }

        if (!found) return;

        boolean dimensionOk = (world.provider.dimensionId == 1);
        boolean chestNorthContentsOk;
        boolean chestEastContentsOk;
        boolean chestSouthContentsOk;
        boolean chestWestContentsOk;
        boolean spiralOk = checkSpiral(world, beaconX, beaconY, beaconZ);

        ItemStack[] CHEST_NORTH_CONTENTS = { new ItemStack(Blocks.stone), new ItemStack(Items.brick),
            new ItemStack(Blocks.glass), new ItemStack(Items.cooked_fished), new ItemStack(Blocks.hardened_clay),
            new ItemStack(Items.dye, 1, 2), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.cooked_beef),
            new ItemStack(Items.iron_ingot), new ItemStack(Items.cooked_chicken), new ItemStack(Items.gold_ingot),
            new ItemStack(Items.baked_potato), new ItemStack(Items.cooked_porkchop), new ItemStack(Items.netherbrick) };

        int[] POTION_IDS = { 8193, 8194, 8195, 8196, 8197, 8198, 8200, 8201, 8202, 8204, 8205, 8206, 8225, 8226, 8228,
            8229, 8232, 8233, 8234, 8236, 8257, 8258, 8259, 8260, 8262, 8264, 8265, 8267, 8268, 8269, 8270 };
        ItemStack[] CHEST_EAST_CONTENTS = new ItemStack[62];
        for (int i = 0; i < 31; i++) {
            CHEST_EAST_CONTENTS[2 * i] = new ItemStack(Items.potionitem, 1, POTION_IDS[i]);
            CHEST_EAST_CONTENTS[2 * i + 1] = new ItemStack(Items.potionitem, 1, POTION_IDS[i] + 8192);
        }

        ItemStack[] CHEST_SOUTH_CONTENTS = { new ItemStack(Blocks.grass), new ItemStack(Blocks.lapis_ore),
            new ItemStack(Blocks.dirt), new ItemStack(Blocks.obsidian), new ItemStack(Blocks.sand),
            new ItemStack(Blocks.diamond_ore), new ItemStack(Blocks.gravel), new ItemStack(Blocks.redstone_ore),
            new ItemStack(Blocks.gold_ore), new ItemStack(Blocks.clay), new ItemStack(Blocks.iron_ore),
            new ItemStack(Blocks.emerald_ore), new ItemStack(Blocks.coal_ore) };

        ItemStack[] CHEST_WEST_CONTENTS = { new ItemStack(Items.record_13), new ItemStack(Items.record_mellohi),
            new ItemStack(Items.record_cat), new ItemStack(Items.record_stal), new ItemStack(Items.record_blocks),
            new ItemStack(Items.record_strad), new ItemStack(Items.record_chirp), new ItemStack(Items.record_ward),
            new ItemStack(Items.record_far), new ItemStack(Items.record_11), new ItemStack(Items.record_mall),
            new ItemStack(Items.record_wait) };

        if (world.getTileEntity(beaconX, beaconY, beaconZ - 5) instanceof TileEntityChest chest) {
            chestNorthContentsOk = checkChest(chest, CHEST_NORTH_CONTENTS, InversionConfig.northChestRequiredItems);
        } else {
            chestNorthContentsOk = false;
        }
        if (world.getTileEntity(beaconX + 5, beaconY, beaconZ) instanceof TileEntityChest chest) {
            chestEastContentsOk = checkChest(chest, CHEST_EAST_CONTENTS, InversionConfig.eastChestRequiredItems);
        } else {
            chestEastContentsOk = false;
        }
        if (world.getTileEntity(beaconX, beaconY, beaconZ + 5) instanceof TileEntityChest chest) {
            chestSouthContentsOk = checkChest(chest, CHEST_SOUTH_CONTENTS, InversionConfig.southChestRequiredItems);
        } else {
            chestSouthContentsOk = false;
        }
        if (world.getTileEntity(beaconX - 5, beaconY, beaconZ) instanceof TileEntityChest chest) {
            chestWestContentsOk = checkChest(chest, CHEST_WEST_CONTENTS, InversionConfig.westChestRequiredItems);
        } else {
            chestWestContentsOk = false;
        }

        if (!(dimensionOk && spiralOk
            && chestNorthContentsOk
            && chestEastContentsOk
            && chestSouthContentsOk
            && chestWestContentsOk)) {
            return;
        }

        // Ritual has now succeeded

        if (!world.isRemote && !source.siege) {
            player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.complete"));
        }
        if (!source.siege) {
            siegeStart(world, beaconX, beaconY, beaconZ, player);
        }
    }

    public ItemInversionSigilActive() {
        super();
        setUnlocalizedName("inversion_sigil_active");
        setTextureName("utilitiesinexcess:inversion_sigil_active");
        setMaxStackSize(1);
        setContainerItem(this);

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List<ItemStack> p_150895_3_) {
        p_150895_3_.add(getStack());
    }

    public static @NotNull ItemStack getStack() {
        ItemStack stack = new ItemStack(ModItems.INVERSION_SIGIL_ACTIVE.get(), 1);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(DURABILITY_NBT_KEY, awakenedInversionDurability);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        if (awakenedInversionDurability == 0) return itemStack;

        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) return null;

        int uses = tag.getInteger(DURABILITY_NBT_KEY);
        if (uses == 1) return new ItemStack(ModItems.INVERSION_SIGIL_INACTIVE.get(), 1);

        tag.setInteger(DURABILITY_NBT_KEY, uses - 1);

        return itemStack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tt, boolean p_77624_4_) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && awakenedInversionDurability != 0) {
            tt.add(
                StatCollector
                    .translateToLocalFormatted("item.inversion_sigil_active.desc", tag.getInteger(DURABILITY_NBT_KEY)));
        }
        super.addInformation(stack, player, tt, p_77624_4_);
    }
}
