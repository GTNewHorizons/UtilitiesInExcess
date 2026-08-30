package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.NodeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.CommonProxy.energyNodeRenderID;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic.isValidConnectable;

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
        return ModelISBRH.JSON_ISBRH_ID;
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
        return new TileEntityEnergyTransferNode();

    }

    @Override
    public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        return 0b111111;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z)
    {
        int mask = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (getConnection(world, x, y, z, dir))
            {
                mask |= 1 << dir.ordinal();
            }
        }
        return mask;
    }

    @Override
    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
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

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return new DefaultInserter();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);

        if (!(block instanceof BlockTransferNodeEnergy energyNode))
        {
            return;
        }

        int mask = energyNode.getConnectionMask(world, x, y, z);
        AxisAlignedBB bb = getBoundsAABB(mask);
        block.setBlockBounds(
            (float) bb.minX,
            (float) bb.minY,
            (float) bb.minZ,
            (float) bb.maxX,
            (float) bb.maxY,
            (float) bb.maxZ
        );
    }

    public static AxisAlignedBB getBoundsAABB(int connectionMask)
    {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
            0.1875, 0.1875, 0.1875,
            0.8125, 0.8125, 0.8125
        );

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if ((connectionMask & (1 << direction.ordinal())) == 0)
            {
                continue;
            }

            switch (direction) {
                case DOWN:
                    bb.minY = 0.0;
                    break;

                case UP:
                    bb.maxY = 1.0;
                    break;

                case NORTH:
                    bb.minZ = 0.0;
                    break;

                case SOUTH:
                    bb.maxZ = 1.0;
                    break;

                case WEST:
                    bb.minX = 0.0;
                    break;

                case EAST:
                    bb.maxX = 1.0;
                    break;
            }
        }
        return bb;
    }
}
