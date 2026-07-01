package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPacifistsBench;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPacifistsBench extends BlockContainer {

    public BlockPacifistsBench() {
        super(Material.wood);
        setBlockTextureName("utilitiesinexcess:pacifists_bench");
        setBlockName("pacifists_bench");
        setHardness(1F);
    }

    IIcon[] icons = new IIcon[6];

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_west");
        icons[0] = reg.registerIcon(this.getTextureName() + "_bottom");
        icons[1] = reg.registerIcon(this.getTextureName() + "_top");
        icons[2] = reg.registerIcon(this.getTextureName() + "_north");
        icons[3] = reg.registerIcon(this.getTextureName() + "_south");
        icons[4] = blockIcon;
        icons[5] = reg.registerIcon(this.getTextureName() + "_east");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int s, int meta) {
        switch (s) {
            case 0:
            case 1:
                return icons[s];
            default:
                int r = meta + 1;
                r = r > 3 ? r - 4 : r;

                ForgeDirection d = ForgeDirection.getOrientation(s);
                for (int i = 0; i < r; i++) {
                    d = d.getRotation(ForgeDirection.DOWN);
                }

                return icons[d.ordinal()];
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPacifistsBench();
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 10;
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return BlockConfig.pacifistsBench.enablePacifistsBench;
        }

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            if (event.source.getEntity() == null) return;
            NBTTagCompound tag = event.source.getEntity()
                .getEntityData();
            if (!tag.getBoolean("isPacifistsBench")) return;
            TileEntity te = event.entityLiving.worldObj
                .getTileEntity(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
            if (te instanceof TileEntityPacifistsBench table) {
                if (event.entityLiving instanceof EntityLiving living) living.experienceValue = 0;
                for (EntityItem drop : event.drops) {
                    table.receiveItemStack(drop.getEntityItem());
                }
                event.setCanceled(true);
            }
        }
    }
}
