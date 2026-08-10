package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import appeng.block.AEBaseItemBlock;
import appeng.fmp.PartRegistry;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McMultipartSPH;
import com.fouristhenumber.utilitiesinexcess.common.items.BaseTransferItemBlock;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;

import codechicken.lib.data.MCDataInput;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

// This is basically the part factory.
public class Content implements MultiPartRegistry.IPartFactory2 {

    public static final String[] partNames = new String[] { FencePart.name, WallPart.name, SpherePart.name };
    public static final Map<String, Integer> partMap = new HashMap<>();

    public static final Set<String> sidedParts = new HashSet<>();

    public static final Map<String, String> legacyAliases = new HashMap<>();

    public void init() {
        for (int i = 0; i < partNames.length; i++) {
            partMap.put(partNames[i], i);
        }
        sidedParts.add(WallPart.name);
        sidedParts.add(FencePart.name);

        if (!Mods.ExtraUtilities.isLoaded() && OtherConfig.enableWorldConversion) {
            legacyAliases.put("extrautils:sphere", SpherePart.name);
            legacyAliases.put("extrautils:fence", FencePart.name);
            legacyAliases.put("extrautils:wall", WallPart.name);
        }

        Set<String> namesToRegister = new HashSet<>();
        namesToRegister.addAll(Arrays.asList(partNames));
        namesToRegister.addAll(legacyAliases.keySet());

        MultiPartRegistry.registerParts(this, namesToRegister.toArray(new String[0]));
    }

    private String translateName(String name) {
        return legacyAliases.getOrDefault(name, name);
    }

    public static UEMultipart createUEMultiPart(boolean isClient, int side, int material, String name) {
        switch (name) {
            case ("ue_fence"): {
                return new FencePart(material, side);
            }
            case ("ue_wall"): {
                return new WallPart(material, side);
            }
            case ("ue_sphere"): {
                return new SpherePart(material);
            }
        }
        return null;
    }

    // Called on the server
    @Override
    public TMultiPart createPart(String name, NBTTagCompound nbt) {
        String actualName = translateName(name);

        return createUEMultiPart(
            false,
            nbt.getInteger("side"),
            MicroMaterialRegistry.materialID(nbt.getString("material")),
            actualName);
    }

    // Called on the client
    @Override
    public TMultiPart createPart(String name, MCDataInput packet) {
        String actualName = translateName(name);

        if (sidedParts.contains(actualName)) {
            return createUEMultiPart(true, packet.readInt(), MicroMaterialRegistry.readMaterialID(packet), actualName);
        }
        return createUEMultiPart(true, 0, MicroMaterialRegistry.readMaterialID(packet), actualName);
    }

    // Pretty much ripped from fmp
    // This class deals with the conversion of normal blocks to FMP blocks
    @EventBusSubscriber(side = Side.CLIENT)
    public static class EventHandler
    {
        @EventBusSubscriber.Condition
        public static boolean shouldEnable()
        {
            return Mods.ForgeMicroBlock.isLoaded();
        }

        private static ThreadLocal<Object> placing = new ThreadLocal<>();

        @SubscribeEvent
        public static void playerInteract(PlayerInteractEvent event) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (placing.get() != null) return; // for mods that do dumb stuff and call this event like MFR
                placing.set(event);
                if (place(event.entityPlayer, event.entityPlayer.worldObj)) event.setCanceled(true);
                placing.set(null);
            }
        }

        public static boolean place(EntityPlayer player, World world) {
            MovingObjectPosition hit = RayTracer.reTrace(world, player);
            if (hit == null) return false;

            final BlockCoord pos = new BlockCoord(hit.blockX, hit.blockY, hit.blockZ).offset(hit.sideHit);
            final ItemStack held = player.getHeldItem();

            if (held == null) {
                return false;
            }

            Block itemBlock = null;
            TMultiPart part = null;
            if (held.getItem() instanceof BaseTransferItemBlock item) {
                itemBlock = Block.getBlockFromItem(item);
                part = ConversionRegistry.getPartByBlock(itemBlock, hit.sideHit);
            }

            if (part == null) return false;

            if (world.isRemote && !player.isSneaking()) // attempt to use block activated like normal and tell the server
            // the right stuff
            {
                Vector3 f = new Vector3(hit.hitVec).add(-hit.blockX, -hit.blockY, -hit.blockZ);
                Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
                if (!ignoreActivate(block) && block.onBlockActivated(
                    world,
                    hit.blockX,
                    hit.blockY,
                    hit.blockZ,
                    player,
                    hit.sideHit,
                    (float) f.x,
                    (float) f.y,
                    (float) f.z)) {
                    player.swingItem();
//                    PacketCustom.sendToServer(
//                        new C08PacketPlayerBlockPlacement(
//                            hit.blockX,
//                            hit.blockY,
//                            hit.blockZ,
//                            hit.sideHit,
//                            player.inventory.getCurrentItem(),
//                            (float) f.x,
//                            (float) f.y,
//                            (float) f.z));
                    return true;
                }
            }

            TileMultipart tile = TileMultipart.getOrConvertTile(world, pos);
            if (tile == null || !tile.canAddPart(part)) return false;

            if (!world.isRemote) {
                TileMultipart.addPart(world, pos, part);
                world.playSoundEffect(
                    pos.x + 0.5,
                    pos.y + 0.5,
                    pos.z + 0.5,
                    itemBlock.stepSound.func_150496_b(),
                    (itemBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                    itemBlock.stepSound.getPitch() * 0.8F);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (held.stackSize == 0) {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, held));
                    }
                }
            } else {
                player.swingItem();
//                new PacketCustom(McMultipartSPH.channel, 1).sendToServer();
            }
            return true;
        }

        /**
         * Because vanilla is weird.
         */
        private static boolean ignoreActivate(Block block) {
            if (block instanceof BlockFence) return true;
            return false;
        }
    }
}
