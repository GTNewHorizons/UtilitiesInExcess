package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static net.minecraftforge.common.util.ForgeDirection.*;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityGigaTorch;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BlockGigaTorch extends BlockContainer {

    public BlockGigaTorch() {
        super(Material.circuits);
        setBlockName("giga_torch");
        setHardness(1.2F);
        setLightLevel(0.9375F); // 15 light level
        setStepSound(soundTypeWood);
        this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityGigaTorch();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        if (World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)) {
            return true;
        } else {
            Block block = worldIn.getBlock(x, y - 1, z);
            return block.canPlaceTorchOnTop(worldIn, x, y - 1, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canPlaceBlockAt(worldIn, x, y, z) && worldIn.getBlock(x, y, z) == this) {
            this.dropBlockAsItem(worldIn, x, y, z, 0, 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    public static class ItemBlockGigaTorch extends ItemBlock {

        public ItemBlockGigaTorch(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted("uie.desc.tile.giga_torch.0", BlockConfig.gigaTorch.gigaTorchRange));
        }
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return BlockConfig.gigaTorch.enableGigaTorch || BlockConfig.chandelier.enableChandelier;
        }

        // context https://github.com/GTNewHorizons/GT5-Unofficial/pull/905
        @SubscribeEvent
        public static void denySpawn(LivingSpawnEvent.CheckSpawn event) {
            if (event.getResult() == Event.Result.DENY) return;

            if (event.entityLiving instanceof EntitySlime slime && !slime.hasCustomNameTag()
                && event.getResult() == Event.Result.ALLOW) {
                event.setResult(Event.Result.DEFAULT);
            }

            if (event.getResult() == Event.Result.ALLOW) {
                return;
            }

            if (event.entityLiving.isCreatureType(EnumCreatureType.monster, false)) {
                if (UtilitiesInExcess.proxy.mobSpawnBlockChecks.isInRange(
                    event.entity.worldObj.provider.dimensionId,
                    event.entity.posX,
                    event.entity.posY,
                    event.entity.posZ)) {
                    if (event.entityLiving instanceof EntitySlime slime) {
                        slime.setCustomNameTag("DoNotSpawnSlimes");
                    }
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}
