package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySmartPump;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class BlockSmartPump extends BlockContainer implements IWailaDataProvider {

    public BlockSmartPump() {
        super(Material.iron);
        setBlockName("smart_pump");
        setBlockTextureName("utilitiesinexcess:smart_pump");
    }

    IIcon sides;
    IIcon top;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        sides = reg.registerIcon("utilitiesinexcess:smart_pump");
        top = reg.registerIcon("utilitiesinexcess:smart_pump_top");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int s, int meta) {
        if (s == ForgeDirection.UP.ordinal() || s == ForgeDirection.DOWN.ordinal()) {
            return top;
        } else {
            return sides;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySmartPump();
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        if (!tag.getBoolean("finished")) {
            currentTip.add(
                StatCollector.translateToLocalFormatted(
                    "tile.smart_pump.waila.working",
                    tag.getInteger("workingX") + " " + tag.getInteger("workingY") + " " + tag.getInteger("workingZ")));
        } else {
            currentTip.add(StatCollector.translateToLocal("tile.smart_pump.waila.finished"));
        }
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        TileEntitySmartPump pump = (TileEntitySmartPump) te;
        tag.setInteger("workingX", pump.getWorkingX());
        tag.setInteger("workingY", pump.getWorkingY());
        tag.setInteger("workingZ", pump.getWorkingZ());
        tag.setBoolean("finished", pump.isFinished());
        return tag;
    }

    // Stubs
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }

    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }
}
