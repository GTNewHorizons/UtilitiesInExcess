package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.List;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    public interface IMerchantRecipeExtension {

        boolean uie$getFavorite();

        void uie$setFavorite(boolean value);

        EntityPlayer uie$getPlayer();

        void uie$setPlayer(EntityPlayer player);

        IMerchant uie$getMerchant();

        void uie$setMerchant(IMerchant merchant);

        int uie$getListIndex();

        void uie$setListIndex(int index);

        default MerchantRecipeList uie$getList() {
            return this.uie$getMerchant()
                .getRecipes(uie$getPlayer());
        }

        default MerchantRecipe uie$getSelf() {
            return (MerchantRecipe) this.uie$getList()
                .get(this.uie$getListIndex());
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("trading_post");
        // Baby villagers shouldn't trade :P
        AxisAlignedBB boundingBox = data.getTileEntity()
            .getRenderBoundingBox()
            .expand(
                32,
                data.getWorld()
                    .getHeight(),
                32);
        List<IMerchant> l = data.getWorld()
            .selectEntitiesWithinAABB(IMerchant.class, boundingBox, entity -> {
                if (entity instanceof EntityAgeable ageable) return ageable.getGrowingAge() >= 0;
                return true;
            });
        return panel;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @ApiStatus.Internal
    public static void handleMerchant(MerchantRecipeList list, IMerchant merchant, EntityPlayer player) {
        for (int i = 0; i < list.size(); i++) {
            MerchantRecipe recipe = (MerchantRecipe) list.get(i);
            IMerchantRecipeExtension er = (IMerchantRecipeExtension) recipe;
            er.uie$setPlayer(player);
            er.uie$setMerchant(merchant);
            er.uie$setListIndex(i);
        }
    }
}
