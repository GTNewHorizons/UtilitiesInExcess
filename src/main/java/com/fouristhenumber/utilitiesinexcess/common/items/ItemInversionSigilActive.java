package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig.awakenedInversionDurability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
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
    private final int[][] LIGHTNING_POSITIONS = { { 0, 0 }, { -5, 0 }, { 5, 0 }, { 0, -5 }, { 0, 5 } };

    private final ItemStack[] CHEST_NORTH_CONTENTS = { new ItemStack(Blocks.stone), new ItemStack(Items.brick),
        new ItemStack(Blocks.glass), new ItemStack(Items.cooked_fished), new ItemStack(Blocks.hardened_clay),
        new ItemStack(Items.dye, 1, 2), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.cooked_beef),
        new ItemStack(Items.iron_ingot), new ItemStack(Items.cooked_chicken), new ItemStack(Items.gold_ingot),
        new ItemStack(Items.baked_potato), new ItemStack(Items.cooked_porkchop), new ItemStack(Items.netherbrick) };

    private final int[] POTION_IDS = { 8193, 8194, 8195, 8196, 8197, 8198, 8200, 8201, 8202, 8204, 8205, 8206, 8225,
        8226, 8228, 8229, 8232, 8233, 8234, 8236, 8257, 8258, 8259, 8260, 8262, 8264, 8265, 8267, 8268, 8269, 8270 };

    private final ItemStack[] CHEST_EAST_CONTENTS = new ItemStack[62];

    private final ItemStack[] CHEST_SOUTH_CONTENTS = { new ItemStack(Blocks.grass), new ItemStack(Blocks.lapis_ore),
        new ItemStack(Blocks.dirt), new ItemStack(Blocks.obsidian), new ItemStack(Blocks.sand),
        new ItemStack(Blocks.diamond_ore), new ItemStack(Blocks.gravel), new ItemStack(Blocks.redstone_ore),
        new ItemStack(Blocks.gold_ore), new ItemStack(Blocks.clay), new ItemStack(Blocks.iron_ore),
        new ItemStack(Blocks.emerald_ore), new ItemStack(Blocks.coal_ore) };

    private final ItemStack[] CHEST_WEST_CONTENTS = { new ItemStack(Items.record_13),
        new ItemStack(Items.record_mellohi), new ItemStack(Items.record_cat), new ItemStack(Items.record_stal),
        new ItemStack(Items.record_blocks), new ItemStack(Items.record_strad), new ItemStack(Items.record_chirp),
        new ItemStack(Items.record_ward), new ItemStack(Items.record_far), new ItemStack(Items.record_11),
        new ItemStack(Items.record_mall), new ItemStack(Items.record_wait) };

    public ItemInversionSigilActive() {
        super();
        setUnlocalizedName("inversion_sigil_active");
        setTextureName("utilitiesinexcess:inversion_sigil_active");
        setMaxStackSize(1);
        setContainerItem(this);

        for (int i = 0; i < 31; i++) {
            CHEST_EAST_CONTENTS[2 * i] = new ItemStack(Items.potionitem, 1, POTION_IDS[i]);
            CHEST_EAST_CONTENTS[2 * i + 1] = new ItemStack(Items.potionitem, 1, POTION_IDS[i] + 8192);
        }

        ItemInversionSigilActiveEvents eventHandler = new ItemInversionSigilActiveEvents();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(eventHandler);
    }

    private EntitySiegeProperty getProperties(EntityPlayer player) {
        return (EntitySiegeProperty) player.getExtendedProperties(EntitySiegeProperty.PROP_KEY);
    }

    private List<EntityPlayer> getSiegePlayers() {
        List<EntityPlayer> siegePlayers = new ArrayList<>();
        if (DimensionManager.getWorld(1) == null) {
            return siegePlayers;
        }
        for (EntityPlayer player : DimensionManager.getWorld(1).playerEntities) {
            EntitySiegeProperty properties = getProperties(player);
            if (properties != null && properties.siege) {
                siegePlayers.add(player);
            }
        }
        return siegePlayers;
    }

    private boolean checkSpiral(World world, int x, int y, int z) {
        ForgeDirection[] directions = { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH,
            ForgeDirection.WEST };
        boolean clockwise = true;
        boolean counterClockwise = true;
        Block previousBlock = null;

        for (ForgeDirection direction : directions) {
            int partX = x + direction.offsetX;
            int partY = y;
            int partZ = z + direction.offsetZ;
            Block block = world.getBlock(partX, partY, partZ);

            if (block != Blocks.redstone_wire && block != Blocks.tripwire) {
                return false;
            }
            if (block == previousBlock) {
                return false;
            }

            // Every next part of a spiral should have an alternated block
            previousBlock = block;

            if (clockwise && checkSpiralPart(world, partX, partY, partZ, rotateClockwise(direction), block)) {
                counterClockwise = false;
                continue;
            }
            if (counterClockwise
                && checkSpiralPart(world, partX, partY, partZ, rotateCounterClockwise(direction), block)) {
                clockwise = false;
                continue;
            }

            return false;
        }

        return true;
    }

    private boolean checkSpiralPart(World world, int x, int y, int z, ForgeDirection direction, Block block) {
        int[] SPIRAL_SEGMENT_LENGTHS = { 3, 5, 7, 8 };

        for (int segmentLength : SPIRAL_SEGMENT_LENGTHS) {
            for (int i = 0; i < segmentLength; i++) {
                if (world.getBlock(x, y, z) != block) {
                    return false;
                }

                if (i != segmentLength - 1) {
                    x += direction.offsetX;
                    z += direction.offsetZ;
                }
            }

            direction = rotateClockwise(direction);
        }

        return true;
    }

    private ForgeDirection rotateClockwise(ForgeDirection direction) {
        return switch (direction) {
            case NORTH -> ForgeDirection.EAST;
            case EAST -> ForgeDirection.SOUTH;
            case SOUTH -> ForgeDirection.WEST;
            case WEST -> ForgeDirection.NORTH;
            // should not happen
            default -> direction;
        };
    }

    private ForgeDirection rotateCounterClockwise(ForgeDirection direction) {
        return switch (direction) {
            case NORTH -> ForgeDirection.WEST;
            case EAST -> ForgeDirection.NORTH;
            case SOUTH -> ForgeDirection.EAST;
            case WEST -> ForgeDirection.SOUTH;
            // should not happen
            default -> direction;
        };
    }

    private void startSiege(World world, int beaconX, int beaconY, int beaconZ, EntityPlayer player) {
        EntitySiegeProperty source = getProperties(player);
        source.beaconSpawnX = beaconX;
        source.beaconSpawnY = beaconY;
        source.beaconSpawnZ = beaconZ;
        source.siege = true;
        source.siegeMobsKilled = 0;
        source.siegeTimer = 0;
        for (Entity curentity : player.getEntityWorld().loadedEntityList) {
            if (curentity instanceof EntityEnderman) {
                curentity.setDead();
            }
        }
        for (int i = 0; i < LIGHTNING_POSITIONS.length; i++) {
            EntityLightningBolt lightningBolt = new EntityLightningBolt(
                world,
                beaconX + LIGHTNING_POSITIONS[i][0] + 0.5,
                beaconY + 0.5,
                beaconZ + LIGHTNING_POSITIONS[i][1] + 0.5);
            world.addWeatherEffect(lightningBolt);
            world.setBlock(
                beaconX + LIGHTNING_POSITIONS[i][0],
                beaconY,
                beaconZ + LIGHTNING_POSITIONS[i][1],
                Blocks.air);
        }
    }

    private void endSiege(boolean won, EntityPlayer player) {
        EntitySiegeProperty source = getProperties(player);
        source.siege = false;
        source.siegeMobsKilled = 0;
        source.siegeTimer = 0;
        for (Entity curentity : player.getEntityWorld().loadedEntityList) {
            if (curentity instanceof EntityMob) {
                curentity.setDead();
            }
        }
        if (won) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() == this) {
                    player.inventory.setInventorySlotContents(i, ModItems.PSEUDO_INVERSION_SIGIL.newItemStack());
                    break;
                }
            }
        }
    }

    private boolean checkChest(TileEntityChest chest, ItemStack[] itemsToCheck, int requiredAmount) {
        int foundItemsAmount = 0;
        boolean[] hasItem = new boolean[itemsToCheck.length];
        for (int i = 0; i < chest.getSizeInventory(); i++) {
            ItemStack stack = chest.getStackInSlot(i);
            for (int j = 0; j < itemsToCheck.length; j++) {
                if (stack != null && ItemStack.areItemStacksEqual(stack, itemsToCheck[j])) {
                    hasItem[j] = true;
                    break;
                }
            }
        }
        for (int i = 0; i < itemsToCheck.length; i++) {
            if (hasItem[i]) {
                foundItemsAmount++;
            }
        }
        return foundItemsAmount >= requiredAmount;
    }

    private boolean checkChestInDirection(ForgeDirection direction, int beaconX, int beaconY, int beaconZ,
        World world) {
        ItemStack[] contents;
        int requiredAmount;

        if (direction == ForgeDirection.NORTH) {
            beaconZ -= 5;
            contents = CHEST_NORTH_CONTENTS;
            requiredAmount = InversionConfig.northChestRequiredItems;
        } else if (direction == ForgeDirection.SOUTH) {
            beaconZ += 5;
            contents = CHEST_SOUTH_CONTENTS;
            requiredAmount = InversionConfig.southChestRequiredItems;
        } else if (direction == ForgeDirection.EAST) {
            beaconX += 5;
            contents = CHEST_EAST_CONTENTS;
            requiredAmount = InversionConfig.eastChestRequiredItems;
        } else if (direction == ForgeDirection.WEST) {
            beaconX -= 5;
            contents = CHEST_WEST_CONTENTS;
            requiredAmount = InversionConfig.westChestRequiredItems;
        } else {
            throw new IllegalArgumentException("Invalid direction passed: " + direction);
        }

        if (world.getTileEntity(beaconX, beaconY, beaconZ) instanceof TileEntityChest chest) {
            return checkChest(chest, contents, requiredAmount);
        }
        return false;
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
        boolean spiralOk = checkSpiral(world, x, y, z);
        boolean chestNorthContentsOk = checkChestInDirection(ForgeDirection.NORTH, x, y, z, world);
        boolean chestEastContentsOk = checkChestInDirection(ForgeDirection.EAST, x, y, z, world);
        boolean chestSouthContentsOk = checkChestInDirection(ForgeDirection.SOUTH, x, y, z, world);
        boolean chestWestContentsOk = checkChestInDirection(ForgeDirection.WEST, x, y, z, world);

        player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.header"));
        if (dimensionOk) {
            player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.correctDimension"));
        } else if (world.provider.dimensionId == 0) {
            player.addChatMessage(
                new ChatComponentTranslation("chat.pseudo_inversion_ritual.incorrectDimensionOverworld"));
        } else if (world.provider.dimensionId == -1) {
            player
                .addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.incorrectDimensionNether"));
        } else {
            player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.incorrectDimensionOther"));
        }
        player.addChatMessage(
            new ChatComponentTranslation(
                "chat.pseudo_inversion_ritual.chestNorthContents",
                chestNorthContentsOk ? "✓" : "✗"));
        player.addChatMessage(
            new ChatComponentTranslation(
                "chat.pseudo_inversion_ritual.chestEastContents",
                chestEastContentsOk ? "✓" : "✗"));
        player.addChatMessage(
            new ChatComponentTranslation(
                "chat.pseudo_inversion_ritual.chestSouthContents",
                chestSouthContentsOk ? "✓" : "✗"));
        player.addChatMessage(
            new ChatComponentTranslation(
                "chat.pseudo_inversion_ritual.chestWestContents",
                chestWestContentsOk ? "✓" : "✗"));
        player
            .addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.spiral", spiralOk ? "✓" : "✗"));
        if (dimensionOk && chestNorthContentsOk
            && chestEastContentsOk
            && chestSouthContentsOk
            && chestWestContentsOk
            && spiralOk) {
            player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.ready"));
        }
        return true;
    }

    private boolean isValidRitualBeacon(World world, int beaconX, int beaconY, int beaconZ) {
        boolean dimensionOk = (world.provider.dimensionId == 1);

        return dimensionOk && world.getBlock(beaconX, beaconY, beaconZ) == Blocks.beacon
            && checkSpiral(world, beaconX, beaconY, beaconZ)
            && checkChestInDirection(ForgeDirection.NORTH, beaconX, beaconY, beaconZ, world)
            && checkChestInDirection(ForgeDirection.EAST, beaconX, beaconY, beaconZ, world)
            && checkChestInDirection(ForgeDirection.SOUTH, beaconX, beaconY, beaconZ, world)
            && checkChestInDirection(ForgeDirection.WEST, beaconX, beaconY, beaconZ, world);
    }

    public class ItemInversionSigilActiveEvents {

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void whenServerTick(TickEvent.ServerTickEvent event) {

            List<EntityPlayer> playerList = getSiegePlayers();
            for (EntityPlayer player : playerList) {
                World world = player.getEntityWorld();

                EntitySiegeProperty properties = getProperties(player);
                if (--properties.siegeTimer > 0) {
                    continue;
                }
                properties.siegeTimer = 30 + world.rand.nextInt(21);

                int mobType = world.rand.nextInt(4);
                EntityMob entityMob = switch (mobType) {
                    case 0 -> {
                        int zombieType = world.rand.nextInt(25);
                        if (zombieType == 0) {
                            yield new EntityGiantZombie(world);
                        } else {
                            yield new EntityZombie(world);
                        }
                    }
                    case 1 -> new EntitySkeleton(world);
                    case 2 -> new EntitySpider(world);
                    case 3 -> new EntityCreeper(world);
                    default -> throw new IllegalStateException("Unexpected value: " + mobType);
                };

                int offsetX = world.rand.nextInt(101) - 50;
                int offsetZ = world.rand.nextInt(101) - 50;

                int mobX = (int) player.posX + offsetX;
                int mobY = 0;
                int mobZ = (int) player.posZ + offsetZ;

                for (int y = (int) player.posY + 10; y >= 0; y--) {
                    if (!World.doesBlockHaveSolidTopSurface(world, mobX, y, mobZ)) {
                        continue;
                    }

                    entityMob.setPosition(mobX, y + 1, mobZ);

                    if (!world.getCollidingBoundingBoxes(entityMob, entityMob.boundingBox)
                        .isEmpty()) {
                        continue;
                    }
                    if (!world.checkNoEntityCollision(entityMob.boundingBox)) {
                        break;
                    }

                    mobY = y + 1;
                    break;
                }

                if (mobY != 0) {
                    double damage = entityMob instanceof EntityGiantZombie ? 16D : 8D;
                    entityMob.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                        .setBaseValue(damage);
                    world.spawnEntityInWorld(entityMob);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void whenPlayerLeavesEnd(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (getProperties(event.player).siege) {
                event.player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.leftEnd"));
                endSiege(false, event.player);
            }
        }

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void whenPlayerLeavesEnd2(PlayerEvent.PlayerRespawnEvent event) {
            if (getProperties(event.player).siege) {
                event.player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.leftEnd"));
                endSiege(false, event.player);
            }
        }

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void whenEndermanSpawn(LivingSpawnEvent.CheckSpawn event) {
            if (event.world.provider.dimensionId == 1 && event.entity instanceof EntityEnderman
                && !getSiegePlayers().isEmpty()) {
                event.setResult(Event.Result.DENY);
            }
        }

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onLivingDeath(LivingDeathEvent event) {

            World world = event.entityLiving.worldObj;

            if (event.entityLiving instanceof EntityPlayer deadplayer && getProperties(deadplayer).siege) {
                deadplayer.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.death"));
                endSiege(false, deadplayer);
                return;
            }
            if (event.source == null || !(event.source.getSourceOfDamage() instanceof EntityPlayer player)) {
                return;
            }
            EntitySiegeProperty source = getProperties(player);

            if (world.isRemote) return;

            if (event.entityLiving instanceof EntityMob && source.siege) {
                source.siegeMobsKilled++;
                if (source.siegeMobsKilled >= InversionConfig.siegeRequiredMobsKill) {
                    player.addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.victory"));
                    endSiege(true, player);
                } else if (source.siegeMobsKilled == (3 * InversionConfig.siegeRequiredMobsKill) / 4) {
                    player.addChatMessage(
                        new ChatComponentTranslation(
                            "chat.pseudo_inversion_ritual.threequarters",
                            source.siegeMobsKilled));
                } else if (source.siegeMobsKilled == (InversionConfig.siegeRequiredMobsKill) / 2) {
                    player.addChatMessage(
                        new ChatComponentTranslation(
                            "chat.pseudo_inversion_ritual.twoquarters",
                            source.siegeMobsKilled));
                } else if (source.siegeMobsKilled == (InversionConfig.siegeRequiredMobsKill) / 4) {
                    player.addChatMessage(
                        new ChatComponentTranslation(
                            "chat.pseudo_inversion_ritual.onequarter",
                            source.siegeMobsKilled));
                }
                return;
            }

            if (!(event.entityLiving instanceof EntityIronGolem)) {
                return;
            }

            if (source.siege) return; // Cannot start a second siege while in a siege.
            if (!player.inventory.hasItem(ItemInversionSigilActive.this)) return;

            int radius = BEACON_SEARCH_RADIUS;
            int mobX = (int) Math.floor(event.entityLiving.posX);
            int mobY = (int) Math.floor(event.entityLiving.posY);
            int mobZ = (int) Math.floor(event.entityLiving.posZ);

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        int beaconX = mobX + dx;
                        int beaconY = mobY + dy;
                        int beaconZ = mobZ + dz;

                        if (isValidRitualBeacon(world, beaconX, beaconY, beaconZ)) {
                            // Ritual has now succeeded
                            player
                                .addChatMessage(new ChatComponentTranslation("chat.pseudo_inversion_ritual.complete"));
                            startSiege(world, beaconX, beaconY, beaconZ, player);
                            return;
                        }
                    }
                }
            }
        }
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
