package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.MCHelper;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.mixins.early.minecraft.GuiContainerAccessor;
import com.cleanroommc.modularui.mixins.early.minecraft.GuiScreenAccessor;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.GlStateManager;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {


    public static class TradeWidget extends ParentWidget<TradeWidget>
    {
        private final ArrayList<CachedMerchantTrade> trades;
        private final int index;

        public TradeWidget(ArrayList<CachedMerchantTrade> trades, int index)
        {
            this.trades = trades;
            this.index = index;
            this.coverChildren();

            Flow inputItems=new Row().childPadding(5).coverChildren()
                    .child(new ItemDisplayWidget().displayAmount(true).item(new ObjectValue.Dynamic<>(()->trades.get(index).getInput1(),v->{})))
                    .child(new ItemDisplayWidget().displayAmount(true).item(new ObjectValue.Dynamic<>(()->trades.get(index).getInput2(),v->{})));

            ProgressWidget progress=new ProgressWidget().direction(ProgressWidget.Direction.RIGHT).texture(GuiTextures.PROGRESS_ARROW,20)
                    .progress(()->{
                        var trade=trades.get(index);
                        return 1-(trade.currentUses/(double)trade.maxUses);
                    })
                    ;

            Flow wholeRow=new Row().coverChildren().childPadding(2)
                    .child(inputItems)
                    .child(progress)
                    .child(new ItemDisplayWidget().displayAmount(true).item(new ObjectValue.Dynamic<>(()->trades.get(index).getOutput(),v->{})));
                    ;


            this.child(wholeRow);
        }
    }

    public static class TradeGrid extends Grid {
        private final ArrayList<CachedMerchantTrade> trades;

        public TradeGrid(ArrayList<CachedMerchantTrade> trades)
        {
            this.trades = trades;
            this.mapTo(1, trades.size(),index->new TradeWidget(this.trades,index).padding(5,5));
            //this.row(n);
        }
    }
    public static class EntityDisplay extends Widget<EntityDisplay>
    {
        private final Supplier<EntityLivingBase> entitySupplier;

        public EntityDisplay(Supplier<EntityLivingBase> e)
        {
            this.entitySupplier = e;
            this.size(18,36);
        }
        public static void drawEntity(int x,int y,int scale,float lookAtX,float lookAtY,EntityLivingBase e,int z,ModularGuiContext context)
        {
            GL11.glColor4f(1,1,1,1);
            //GuiInventory.func_147046_a(x,y,scale,0,0,e );
            //int p_147046_0_, int p_147046_1_, int p_147046_2_, float p_147046_3_, float p_147046_4_, EntityLivingBase p_147046_5_
            final int p_147046_0_=x;
            final int p_147046_1_=y;
            final int p_147046_2_=scale;
            final float p_147046_3_=lookAtX;
            final float p_147046_4_=lookAtY;
            final EntityLivingBase p_147046_5_=e;


            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glPushMatrix();
            //GL11.glTranslatef((float)p_147046_0_, (float)p_147046_1_, z);

            float f2 = p_147046_5_.renderYawOffset;
            float f3 = p_147046_5_.rotationYaw;
            float f4 = p_147046_5_.rotationPitch;
            float f5 = p_147046_5_.prevRotationYawHead;
            float f6 = p_147046_5_.rotationYawHead;
            //GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableGUIStandardItemLighting();
            //GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            //GL11.glRotatef(-((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
            p_147046_5_.renderYawOffset = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 20.0F;
            p_147046_5_.rotationYaw = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 40.0F;
            p_147046_5_.rotationPitch = -((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F;
            p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
            p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
            //GL11.glTranslatef(0.0F, p_147046_5_.yOffset, 0.0F);
            RenderManager.instance.playerViewY = 180.0F;
            context.applyToOpenGl();
            GL11.glScalef((float)(-p_147046_2_), (float)p_147046_2_, (float)p_147046_2_);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            RenderManager.instance.renderEntityWithPosYaw(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
            p_147046_5_.renderYawOffset = f2;
            p_147046_5_.rotationYaw = f3;
            p_147046_5_.rotationPitch = f4;
            p_147046_5_.prevRotationYawHead = f5;
            p_147046_5_.rotationYawHead = f6;
            GL11.glPopMatrix();
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);



        }

        @Override
        public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
            EntityLivingBase e=entitySupplier.get();
            //ItemSlot;
            //TODO: not working
            var area=this.getArea();
            drawEntity(0,0,10,0,0,e,area.z(),context);

        }
    }

    public EntityVillager clientVillager;

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        int w=176;
        int h=166;
        ModularPanel panel = ModularPanel.defaultPanel("trading_post",w, h);
        panel.bindPlayerInventory();
        // Add title
        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal("tile.trading_post.name"))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));
        panel.child(new TradeGrid(clientCachedTrades).marginTop(12).size(w-8-77,h-12-8-80).scrollable());
        panel.child(new EntityDisplay(()->clientVillager ));
        return panel;
    }




    public final static class CachedMerchantTrade
    {
        private final MerchantRecipe recipe;
        private final IMerchant merchant;
        private ItemStack input1;
        private ItemStack input2;
        private ItemStack output;
        private int currentUses;
        private int maxUses;
        public  ItemStack getInput1()
        {
            updateCache();
            return input1;
        }
        public  ItemStack getInput2()
        {
            updateCache();
            return input2;
        }
        public  ItemStack getOutput()
        {
            updateCache();
            return output;
        }
        public  int getCurrentUses()
        {
            updateCache();
            return currentUses;
        }
        public  int getMaxUses()
        {
            updateCache();
            return maxUses;
        }
        public boolean isRecipeDisabled()
        {
            updateCache();
            return getCurrentUses() >= getMaxUses() || merchant==null;
        }

        public final void updateCache()
        {
            if(recipe==null)return;
            AccessorMerchantRecipe accessorTrade=(AccessorMerchantRecipe)recipe;
            input1=recipe.getItemToBuy();
            input2=recipe.getSecondItemToBuy();
            output=recipe.getItemToSell();
            currentUses=accessorTrade.getCurrentUses();
            maxUses=accessorTrade.getMaxUses();
        }

        public CachedMerchantTrade(MerchantRecipe trade,IMerchant merchant)
        {
            recipe=trade;
            this.merchant=merchant;
            updateCache();
        }
    }


    public final ArrayList<CachedMerchantTrade> clientCachedTrades=new ArrayList<>();


    public void updateCachedTrades(EntityPlayer player)
    {
        if(!getWorldObj().isRemote)return;
        AxisAlignedBB box=AxisAlignedBB.getBoundingBox(xCoord-32,yCoord-getWorldObj().getHeight(),zCoord-32,xCoord+getWorldObj().getHeight(),yCoord+32,zCoord+32);
        List<IMerchant> foundMerchants = worldObj.getEntitiesWithinAABB(IMerchant.class,box);
        clientCachedTrades.clear();
        for (IMerchant merchant : foundMerchants)
        {
            clientVillager=(EntityVillager)merchant;
            MerchantRecipeList allTrades=merchant.getRecipes(player);
            for (Object _trade : allTrades) {
                MerchantRecipe trade=(MerchantRecipe)_trade;
                CachedMerchantTrade cachedTrade=new CachedMerchantTrade(trade,merchant);
                clientCachedTrades.add(cachedTrade);
            }
        }
    }



    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }


}
