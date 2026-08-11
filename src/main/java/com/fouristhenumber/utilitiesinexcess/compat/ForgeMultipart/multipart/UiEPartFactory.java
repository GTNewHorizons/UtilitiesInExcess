package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TileMultipart;
import com.fouristhenumber.utilitiesinexcess.common.items.BaseTransferItemBlock;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.EnergyNodePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.FluidRetrievalNodePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.FluidTransferNodePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.PipePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.ItemRetrievalNodePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.ItemTransferNodePart;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PacketFMPPlaceBlock;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
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

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry.getPartByBlock;

public class UiEPartFactory implements MultiPartRegistry.IPartFactory2, MultiPartRegistry.IPartConverter {

    public static final String[] materialBasedPartNames = new String[]
        {
            FencePart.name, WallPart.name, SpherePart.name,
        };

    public static final String[] transferPartNames = new String[]
        {
            ConversionRegistry.EnergyNode.getName(), ConversionRegistry.FluidTransferNode.getName(),
            ConversionRegistry.FluidRetrievalNode.getName(), ConversionRegistry.ItemTransferNode.getName(),
            ConversionRegistry.ItemRetrievalNode.getName(), ConversionRegistry.Pipe.getName()
        };

    public static final Map<String, Integer> partMap = new HashMap<>();

    public static final Map<String, String> legacyAliases = new HashMap<>();

    public void init() {
        for (int i = 0; i < materialBasedPartNames.length; i++) {
            partMap.put(materialBasedPartNames[i], i);
        }
        for (int i = 0; i < transferPartNames.length; i++)
        {
            partMap.put(transferPartNames[i], i);
        }

        if (!Mods.ExtraUtilities.isLoaded() && OtherConfig.enableWorldConversion) {
            legacyAliases.put("extrautils:sphere", SpherePart.name);
            legacyAliases.put("extrautils:fence", FencePart.name);
            legacyAliases.put("extrautils:wall", WallPart.name);
        }

        Set<String> namesToRegister = new HashSet<>();
        namesToRegister.addAll(partMap.keySet());
        namesToRegister.addAll(legacyAliases.keySet());

        MultiPartRegistry.registerParts(this, namesToRegister.toArray(new String[0]));
        MultiPartRegistry.registerConverter(this);
    }

    private String translateName(String name) {
        return legacyAliases.getOrDefault(name, name);
    }

    public static UEMultipart createUEMultiPart(boolean isClient, int meta, int material, String name) {
        switch (name) {
            case ("ue_fence"): {
                return new FencePart(material, meta);
            }
            case ("ue_wall"): {
                return new WallPart(material, meta);
            }
            case ("ue_sphere"): {
                return new SpherePart(material);
            }
            case ("item_retrieval_node"):
            {
                return new ItemRetrievalNodePart(meta);
            }
            case ("fluid_retrieval_node"):
            {
                return new FluidRetrievalNodePart(meta);
            }
            case ("item_transfer_node"):
            {
                return new ItemTransferNodePart(meta);
            }
            case ("fluid_transfer_node"):
            {
                return new FluidTransferNodePart(meta);
            }
            case ("pipe"):
            {
                return new PipePart(meta);
            }
            case ("energy_node"):
            {
                return new EnergyNodePart(meta);
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
    public TMultiPart createPart(String name, MCDataInput packet)
    {
        String actualName = translateName(name);

        // Honestly, this is ugly as fuck, but the other option I can think of right now is make maps of them.
        // I see no reason to do this, I just want to finish the damn thing.
        return switch (actualName) {
            case (FencePart.name), (WallPart.name) ->
                createUEMultiPart(true, packet.readInt(), MicroMaterialRegistry.readMaterialID(packet), actualName);
            case (SpherePart.name) ->
                createUEMultiPart(true, 0, MicroMaterialRegistry.readMaterialID(packet), actualName);
            default -> createUEMultiPart(true, packet.readInt(), 0, actualName);
        };
    }

    @Override
    public Iterable<Block> blockTypes() {
        return Arrays.stream(ConversionRegistry.values())
            .map(ConversionRegistry::getBlock)
            .collect(Collectors.toList());
    }

    @Override
    public TMultiPart convert(World world, BlockCoord pos) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
        return getPartByBlock(block, meta);
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
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.world.isRemote) {
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
                part = getPartByBlock(itemBlock, hit.sideHit);
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
                    PacketCustom.sendToServer(
                        new C08PacketPlayerBlockPlacement(
                            hit.blockX,
                            hit.blockY,
                            hit.blockZ,
                            hit.sideHit,
                            player.inventory.getCurrentItem(),
                            (float) f.x,
                            (float) f.y,
                            (float) f.z));
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
                PacketHandler.INSTANCE.sendToServer(new PacketFMPPlaceBlock());
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
