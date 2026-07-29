package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.energyNodeRenderID;

public class BlockTransferNodeEnergy extends BlockTransferBase
{

    public enum EnergyNodeType {
        BASE("transfer_node_energy"),
        HYPER("transfer_node_hyper_energy");

        private final String name;
        private final String textureName;
        private IIcon iicon;

        EnergyNodeType(String name) {
            this.name = name;
            this.textureName = name;
        }

        public void registerIcon(IIconRegister reg) {
            this.iicon = reg.registerIcon("utilitiesinexcess:" + textureName);
        }

        public String getName()
        {
            return name;
        }

        public IIcon getIcon()
        {
            return iicon;
        }
    }

    public BlockTransferNodeEnergy()
    {
        super(Material.iron);
        setBlockName("transfer_node_energy");
    }

    @Override
    public int getRenderType()
    {
        return energyNodeRenderID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (int i = 0; i < EnergyNodeType.values().length; i++)
        {
            EnergyNodeType.values()[i].registerIcon(reg);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return EnergyNodeType.values()[meta].getIcon();
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (int i = 0; i < EnergyNodeType.values().length; i++)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        if (metadata == 0)
        {
            return new TileEntityEnergyTransferNode(false);
        }
        return new TileEntityEnergyTransferNode(true);
    }

    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, int metadata, IWalkingComponent<?> walkingComponent)
    {
        return 0b111111;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z, int metadata)
    {
        int mask = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir))
            {
                mask |= 1 << dir.ordinal();
            }
        }
        return mask;
    }

    @Override
    public boolean acceptsConnectionFrom(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection direction)
    {
        return true;
    }
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
                                    float subY, float subZ) {
        if (!worldIn.isRemote) GuiFactories.tileEntity()
            .open(player, x, y, z);
        return true;
    }

}
