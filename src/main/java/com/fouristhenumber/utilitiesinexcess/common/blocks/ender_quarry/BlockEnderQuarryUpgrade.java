package com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnderQuarryUpgrade extends Block implements IEnderQuarryUpgrade {

    private IIcon[] icons;

    public BlockEnderQuarryUpgrade() {
        super(Material.iron);
        setHardness(1f);
        setLightOpacity(0);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        return 0;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[3];
        icons[0] = reg.registerIcon("utilitiesinexcess:upgrade_speed_1");
        icons[1] = reg.registerIcon("utilitiesinexcess:upgrade_speed_2");
        icons[2] = reg.registerIcon("utilitiesinexcess:upgrade_speed_3");
    }
}
