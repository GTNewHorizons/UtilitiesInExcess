package com.fouristhenumber.utilitiesinexcess.common.events;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@EventBusSubscriber()
public class MobDenySpawnEvents {

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return ModBlocks.GIGA_TORCH.isEnabled();
    }

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
