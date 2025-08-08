package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.WateringCanConfig.wateringCan;
import static net.minecraft.init.Blocks.mycelium;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.IGrowable;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.ParticlePacket;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.lib.FakeThaumcraftPlayer;

// Credit to Mystical Agriculture 1.12 for the original ItemWateringCan implementation
public class ItemWateringCan extends Item {

    private int range;
    private static final String TAG_ACTIVE = "WateringCanActive";
    // A map to track the last time each player used the watering can
    public static final Map<EntityPlayer, Long> lastWaterTick = new WeakHashMap<>();
    private final int cooldownTicks = 4; // Watering delay

    public ItemWateringCan(int tier, int effectArea) {
        // Ensure effectArea is odd, since it should be centered (3, 5, 7, etc.)
        this.range = (effectArea - 1) / 2;;
        setTextureName("utilitiesinexcess:" + getNameFromTier(tier));
        setUnlocalizedName(getNameFromTier(tier));
        setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected || !(entity instanceof EntityPlayer player)) return;
        long currentTick = world.getTotalWorldTime();
        long lastTick = lastWaterTick.getOrDefault(player, 0L);
        // Check if enough time has passed since the last watering action
        boolean canWater = (currentTick - lastTick) >= cooldownTicks;

        // If the player is using the item or the watering can is active, proceed with watering
        if ((player.isUsingItem() || isActive(stack))) {
            // If the player can water and the world is not remote (server-side), perform the watering action
            if (!world.isRemote && canWater) {
                lastWaterTick.put(player, currentTick);
                onItemUse(stack, player, world);
            } else {
                // If the player is remote (client-side) apply the walking speed penalty
                walkingSpeed(player, player.isUsingItem() ? false : true);
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return isActive(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (isFakePlayer(player) && !world.isRemote) {
            this.onItemUse(stack, player, world);
            // prevent CoFH Fake player from deleting the item
            return stack;
        }

        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Check if the watering can is enabled and if the player is sneaking to toggle it
        if (wateringCan.allowWateringCanToggle && player.isSneaking() && !world.isRemote) {
            setActive(stack, !isActive(stack));
            player.addChatMessage(
                new ChatComponentTranslation("item.watering_can." + (isActive(stack) ? "activated" : "deactivated")));
            return true;
        }
        if (isFakePlayer(player) && !world.isRemote) {
            this.onItemUse(stack, player, world, x, y, z, side);
        }

        return false;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {

        if (isFakePlayer(player) && !world.isRemote) {
            this.onItemUse(stack, player, world, x, y, z, side);
        }
        return false;
    }

    /**
     * Called when the item is used by the player.
     * This method is called when the player right-clicks with the item.
     * It performs a ray trace to find the block the player is looking at and calls onItemUse with the hit position.
     */
    public void onItemUse(ItemStack stack, Entity entity, World world) {
        if (entity instanceof EntityPlayer player) {
            MovingObjectPosition hit = getMovingObjectPositionFromPlayer(world, player, true);
            if (hit != null) {
                onItemUse(stack, player, world, hit.blockX, hit.blockY, hit.blockZ, hit.sideHit);
            }
        }
    }

    /**
     * This method is called when the player right-clicks on a block with the item.
     * It checks if the block is a farmland block and if the player has permission to edit it.
     * If so, it performs the watering action on the farmland blocks in the specified range.
     * executed server-side to ensure the watering can works correctly in multiplayer.
     */
    private boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (world.isRemote) return false; // Ensure this is executed server-side
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        int x2 = x + dir.offsetX;
        int y2 = y + dir.offsetY;
        int z2 = z + dir.offsetZ;

        if (!player.canPlayerEdit(x2, y2, z2, side, stack)) return false;

        if (!wateringCan.allowAutomatedWatering && isFakePlayer(player)) return false;

        this.hydrateFarmland(world, x, y, z);

        this.spawnFlowers(world, x, y, z);

        this.spawnParticle(world, x, y, z);

        this.accelerateGrowth(world, x, y, z);

        return false;
    }

    public void accelerateGrowth(World world, int x, int y, int z) {
        int chance = world.rand.nextInt(100) + 1;
        int bonus = 4;
        if (chance <= (40 + bonus)) {
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    for (int dz = -range; dz <= range; dz++) {
                        int bx = x + dx;
                        int by = y + dy;
                        int bz = z + dz;
                        Block plant = world.getBlock(bx, by, bz);
                        if (plant instanceof IGrowable || plant instanceof IPlantable || plant == mycelium) {
                            world.scheduleBlockUpdateWithPriority(bx, by, bz, plant, 0, 1000);
                        }
                    }
                }
            }
        }
    }

    /**
     * Hydrates farmland blocks in a cubic range around the specified coordinates.
     * This method checks each block in the range and sets its metadata to 7 (hydrated) if it is farmland.
     * It is called when the watering can is used to water crops.
     */
    public void hydrateFarmland(World world, int x, int y, int z) {
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    int bx = x + dx;
                    int by = y + dy;
                    int bz = z + dz;
                    if (world.getBlock(bx, by, bz) instanceof BlockFarmland) {
                        int meta = world.getBlockMetadata(bx, by, bz);
                        if (meta < 7) {
                            world.setBlockMetadataWithNotify(bx, by, bz, 7, 2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Spawns flowers in a cubic range around the specified coordinates.
     * If used directly on a flower, it will attempt to multiply that flower nearby.
     * Otherwise, has a chance to spawn a random flower.
     */
    public void spawnFlowers(World world, int x, int y, int z) {
        Block targetBlock = world.getBlock(x, y, z);
        int targetMeta = world.getBlockMetadata(x, y, z);

        // randomly spawn a flower (3% chance)
        if (world.rand.nextInt(100) < 3) {
            int bx = x + world.rand.nextInt(range * 2 + 1) - range;
            int bz = z + world.rand.nextInt(range * 2 + 1) - range;
            int by = y;
            Block targetBlockAt = world.getBlock(bx, by, bz);

            // Multiply flower if used on an existing flower block
            if (wateringCan.Flowering.allowFlowerDuplication && targetBlock instanceof BlockFlower) {
                multiplyFlower(world, bx, by, bz, (BlockFlower) targetBlock, targetMeta);
                return;
            }
            if (!wateringCan.Flowering.allowFlowerSpawning) return;

            // Ensure flowers only spawn on grass blocks:
            // If the target block isn't grass, check the block below its top surface (accounting for block height).
            // Abort spawning if that block is not grass.
            if (targetBlockAt != Blocks.grass) {
                if (world.getBlock(bx, (int) (by - targetBlockAt.getBlockBoundsMaxY()), bz) != Blocks.grass) {
                    return;
                }
            }

            // If the target block height is less than 0.85F try and place a new flower next to it
            if (wateringCan.Flowering.adjustFlowerSpawnHeightForShortBlocks
                && targetBlock.getBlockBoundsMaxY() < 0.85F) {
                by -= targetBlock.getBlockBoundsMaxY(); // Adjust the height to place the flower correctly
            }

            by += 1; // Place the flower one block above the target coordinates
            Block below = world.getBlock(bx, by - 1, bz);

            if (world.isAirBlock(bx, by, bz)
                && below.canSustainPlant(world, bx, by - 1, bz, ForgeDirection.UP, Blocks.red_flower)) {

                int totalVariants = BlockFlower.field_149859_a.length + BlockFlower.field_149858_b.length;
                int variantIndex = world.rand.nextInt(totalVariants);

                Block flowerBlock;
                int meta;

                if (variantIndex < BlockFlower.field_149859_a.length) {
                    flowerBlock = Blocks.red_flower;
                    meta = variantIndex;
                } else {
                    flowerBlock = Blocks.yellow_flower;
                    meta = variantIndex - BlockFlower.field_149859_a.length;
                }

                world.setBlock(bx, by, bz, flowerBlock, meta, 2);
            }
        }
    }

    /**
     * Attempts to multiply a flower near the given coordinates.
     */
    private void multiplyFlower(World world, int x, int y, int z, BlockFlower flowerBlock, int flowerMeta) {
        Block below = world.getBlock(x, y - 1, z);

        if (world.isAirBlock(x, y, z) && below.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, flowerBlock)) {
            world.setBlock(x, y, z, flowerBlock, flowerMeta, 2);
        }
    }

    /**
     * Spawns particles around the watering can to indicate its use.
     * This method is called when the watering can is used to water crops.
     * It sends a particle packet to all players around the watering can.
     */
    public void spawnParticle(World world, int x, int y, int z) {
        Random rand = world.rand;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                double d0 = x + dx + rand.nextFloat();
                double d1 = y + world.getBlock(x, y, z)
                    .getBlockBoundsMaxY();
                double d2 = z + dz + rand.nextFloat();

                if (world.getBlock(x, y, z)
                    .isOpaqueCube() || world.getBlock(x, y, z) instanceof BlockFarmland) {
                    d1 += 0.3D;
                }

                // Send particle packet to all players around the watering can
                PacketHandler.INSTANCE.sendToAllAround(
                    new ParticlePacket("splash", d0, d1, d2, 5),
                    new NetworkRegistry.TargetPoint(world.provider.dimensionId, d0, d1, d2, 32.0 // range in blocks
                    ));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        int range = (this.range * 2 + 1);
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.watering_can.desc.1"));
        tooltip.add(
            EnumChatFormatting.AQUA
                + StatCollector.translateToLocalFormatted("item.watering_can.desc.2", range, range));
        if (wateringCan.allowWateringCanToggle) {
            tooltip.add(
                EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC.toString()
                    + StatCollector.translateToLocal("item.watering_can.desc.3"));
            tooltip.add(
                EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC.toString()
                    + StatCollector.translateToLocal("item.watering_can.desc.4"));
        }
    }

    public boolean isActive(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean(TAG_ACTIVE);
    }

    public void setActive(ItemStack stack, boolean active) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setBoolean(TAG_ACTIVE, active);
    }

    /**
     * Adjusts the player's walking speed based on whether the watering can is active or not.
     * This method is called to apply a speed penalty when the player is using the watering can.
     * It modifies the player's movement input to slow them down while using the watering can.
     */
    public void walkingSpeed(EntityPlayer player, boolean slowness) {
        if (player.worldObj.isRemote && player instanceof EntityPlayerSP playerSP) {
            // this is a workaround to prevent the player from slowing when using the watering can (right click)
            if (!slowness && !wateringCan.walkingSpeedPenalty) {
                playerSP.movementInput.moveStrafe /= 0.2F;
                playerSP.movementInput.moveForward /= 0.2F;
            } else if (slowness && wateringCan.walkingSpeedPenalty) {
                playerSP.movementInput.moveStrafe *= 0.2F;
                playerSP.movementInput.moveForward *= 0.2F;
            }
            playerSP.setSprinting(false); // Disable sprinting
        }
    }

    public String getNameFromTier(int tier) {
        switch (tier) {
            case 1:
                return "watering_can_basic";
            case 2:
                return "watering_can_advanced";
            case 3:
                return "watering_can_elite";
            default:
                return "watering_can"; // Fallback for any other tier
        }
    }

    public boolean isFakePlayer(EntityPlayer player) {
        if (player == null) return false;
        // Check for Forge FakePlayer
        if (player instanceof FakePlayer) return true;
        // Check for Thaumcraft FakeThaumcraftPlayer
        if (Mods.Thaumcraft.isLoaded() && player instanceof FakeThaumcraftPlayer) return true;

        return false;
    }
}
