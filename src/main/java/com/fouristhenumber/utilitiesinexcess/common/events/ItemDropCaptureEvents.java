package com.fouristhenumber.utilitiesinexcess.common.events;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber()
public class ItemDropCaptureEvents {

    private static boolean shouldCaptureDrops = false;
    private static int targetDimensionId;
    private static int targetRadiusSq;
    private static int targetXPos;
    private static int targetYPos;
    private static int targetZPos;
    private static @Nullable ArrayList<ItemStack> capturedItems = null;

    public static void startCapturingDrops(int targetDimensionId, int targetRadius, int targetXPos, int targetYPos,
        int targetZPos) {
        ItemDropCaptureEvents.targetDimensionId = targetDimensionId;
        ItemDropCaptureEvents.targetRadiusSq = targetRadius * targetRadius;
        ItemDropCaptureEvents.targetXPos = targetXPos;
        ItemDropCaptureEvents.targetYPos = targetYPos;
        ItemDropCaptureEvents.targetZPos = targetZPos;
        if (ItemDropCaptureEvents.capturedItems == null) {
            capturedItems = new ArrayList<>();
        }
        ItemDropCaptureEvents.shouldCaptureDrops = true;
    }

    public static ArrayList<ItemStack> stopCapturingAndCollectDrops() {
        if (!shouldCaptureDrops)
            throw new RuntimeException("Tried to collect event item drops without first capturing.");
        shouldCaptureDrops = false;
        ArrayList<ItemStack> returnItems = capturedItems;
        capturedItems = new ArrayList<>();
        return returnItems;
    }

    public static void stopCapturingFinal() {
        if (shouldCaptureDrops) {
            shouldCaptureDrops = false;
            capturedItems = new ArrayList<>();
        }
    }

    @SubscribeEvent
    public static void entityItemSpawnEvent(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && shouldCaptureDrops
            && event.entity instanceof EntityItem entityItem
            && event.world.provider.dimensionId == targetDimensionId
            && capturedItems != null) {
            if (event.entity.getDistanceSq(targetXPos, targetYPos, targetZPos) <= targetRadiusSq) {
                capturedItems.add(entityItem.getEntityItem());
                entityItem.setDead();
                event.setCanceled(true);
            }
        }
    }
}
