package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemInversionSigilInactive extends Item {

    private static final int MIDNIGHT = 18000;
    private static final int WINDOW_TICKS = 1000;
    private static final int ENCHANT_TABLE_SEARCH_RADIUS = 6;
    private static final int GRASS_SEARCH_RADIUS = 4;
    private static final int REQUIRED_GRASS_COUNT = 6;
    private static final int LIGHT_LEVEL_REQUIRED = 4;

    /**
     * If cursed earth is disabled, use dirt instead
     */
    public static Block cursedEarthBlock = (ModBlocks.CURSED_EARTH.isEnabled() ? ModBlocks.CURSED_EARTH.get()
        : Blocks.dirt);

    public ItemInversionSigilInactive() {
        setTextureName("utilitiesinexcess:inversion_sigil_inactive");
        setUnlocalizedName("inversion_sigil_inactive");
        setMaxStackSize(1);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean isWithinMidnightWindow(long time) {
        long low = (MIDNIGHT - WINDOW_TICKS);
        long high = (MIDNIGHT + WINDOW_TICKS);
        return time >= low && time <= high;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            return false;
        }

        Block clicked = world.getBlock(x, y, z);
        if (clicked != Blocks.enchanting_table) {
            return false;
        }

        if (world.isRemote) return true;

        boolean timeOk = isWithinMidnightWindow(world.getWorldTime());
        boolean darkOk = world.getBlockLightValue(x, y, z) <= LIGHT_LEVEL_REQUIRED;
        boolean redstoneRingOk = hasRedstoneRing(world, x, y, z);
        boolean grassOk = countNearbyGrass(world, x, y, z);
        boolean moonOk = world.canBlockSeeTheSky(x, y, z);

        player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("chat.inversion_ritual.header")));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted("chat.inversion_ritual.time", (timeOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector
                    .translateToLocalFormatted("chat.inversion_ritual.redstone", (redstoneRingOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted("chat.inversion_ritual.grass", (grassOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted("chat.inversion_ritual.darkness", (darkOk ? "✓" : "✗"))));
        player.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocalFormatted("chat.inversion_ritual.moon", (moonOk ? "✓" : "✗"))));

        return true;
    }

    private boolean hasRedstoneRing(World world, int tx, int ty, int tz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                int bx = tx + dx;
                int bz = tz + dz;
                Block b = world.getBlock(bx, ty, bz);
                if (b == null || b != Blocks.redstone_wire) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean countNearbyGrass(World world, int cx, int cy, int cz) {
        int count = 0;
        for (int dx = -ItemInversionSigilInactive.GRASS_SEARCH_RADIUS; dx
            <= ItemInversionSigilInactive.GRASS_SEARCH_RADIUS; dx++) {
            for (int dz = -ItemInversionSigilInactive.GRASS_SEARCH_RADIUS; dz
                <= ItemInversionSigilInactive.GRASS_SEARCH_RADIUS; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int bx = cx + dx;
                    int by = cy + dy;
                    int bz = cz + dz;
                    Block b = world.getBlock(bx, by, bz);
                    if (b == Blocks.grass) {
                        count++;
                        if (count > REQUIRED_GRASS_COUNT) return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.entityLiving instanceof EntityWither) {
            event.drops.add(
                new EntityItem(
                    event.entityLiving.worldObj,
                    event.entityLiving.posX,
                    event.entityLiving.posY,
                    event.entityLiving.posZ,
                    new ItemStack(this, 1)));
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingDeath(LivingDeathEvent event) {
        World world = event.entityLiving.worldObj;

        if (world.isRemote) return;

        if (!isWithinMidnightWindow(world.getWorldTime())) return;

        if (!(event.entityLiving instanceof EntityAnimal)) return;

        if (!(event.source != null && event.source.getSourceOfDamage() instanceof EntityPlayer player)) {
            return;
        }

        if (!player.inventory.hasItem(this)) return;

        int radius = ENCHANT_TABLE_SEARCH_RADIUS;
        int mobX = (int) Math.floor(event.entityLiving.posX);
        int mobY = (int) Math.floor(event.entityLiving.posY);
        int mobZ = (int) Math.floor(event.entityLiving.posZ);

        int tableX = 0, tableY = 0, tableZ = 0;
        boolean found = false;

        for (int dx = -radius; dx <= radius && !found; dx++) {
            for (int dy = -2; dy <= 2 && !found; dy++) {
                for (int dz = -radius; dz <= radius && !found; dz++) {
                    int bx = mobX + dx;
                    int by = mobY + dy;
                    int bz = mobZ + dz;
                    if (world.getBlock(bx, by, bz) == Blocks.enchanting_table) {
                        tableX = bx;
                        tableY = by;
                        tableZ = bz;
                        found = true;
                    }
                }
            }
        }

        if (!found) return;

        boolean darkOk = world.getBlockLightValue(tableX, tableY, tableZ) <= LIGHT_LEVEL_REQUIRED;
        boolean redstoneRingOk = hasRedstoneRing(world, tableX, tableY, tableZ);
        boolean grassOk = countNearbyGrass(world, tableX, tableY, tableZ);
        boolean moonOk = world.canBlockSeeTheSky(tableX, tableY, tableZ);

        if (!(darkOk && redstoneRingOk && grassOk && moonOk)) {
            return;
        }

        // Ritual has now succeeded

        EntityLightningBolt bolt = new EntityLightningBolt(world, tableX + 0.5, tableY + 1, tableZ + 0.5);
        world.addWeatherEffect(bolt);

        // Transform all sigils in player's inventory to activated
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack is = player.inventory.getStackInSlot(i);
            if (is != null && is.getItem() == this) {
                player.inventory.setInventorySlotContents(i, ItemInversionSigilActive.getStack());
            }
        }

        // Convert grass to cursed earth
        for (int dx = -GRASS_SEARCH_RADIUS; dx <= GRASS_SEARCH_RADIUS; dx++) {
            for (int dz = -GRASS_SEARCH_RADIUS; dz <= GRASS_SEARCH_RADIUS; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int bx = tableX + dx;
                    int by = tableY + dy;
                    int bz = tableZ + dz;
                    if (world.getBlock(bx, by, bz) == Blocks.grass) {
                        world.setBlock(bx, by, bz, cursedEarthBlock);
                    }
                }
            }
        }

        if (!world.isRemote) {
            player.addChatMessage(new ChatComponentTranslation("chat.inversion_ritual.complete"));
        }
    }
}
