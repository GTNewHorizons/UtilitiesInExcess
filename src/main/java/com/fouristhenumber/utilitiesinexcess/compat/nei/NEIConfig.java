package com.fouristhenumber.utilitiesinexcess.compat.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.Tags;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilActive;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    @Override
    public String getName() {
        return StatCollector.translateToLocal("nei.title.uie.plugin");
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }

    @Override
    public void loadConfig() {
        EnderLocusRecipeHandler enderLocusRecipeHandler = new EnderLocusRecipeHandler();

        API.registerRecipeHandler(enderLocusRecipeHandler);
        API.registerUsageHandler(enderLocusRecipeHandler);

        API.addRecipeCatalyst(ModBlocks.ENDER_LOCUS.newItemStack(), enderLocusRecipeHandler, 1);
        API.addRecipeCatalyst(ModBlocks.CONVERGENCE_CRYSTAL.newItemStack(), enderLocusRecipeHandler, 0);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.ender_locus")
                + "@ender_locus_recipes");

        PseudoReversionRecipeHandler pseudoReversionHandler = new PseudoReversionRecipeHandler();

        API.registerRecipeHandler(pseudoReversionHandler);
        API.registerUsageHandler(pseudoReversionHandler);

        FMLInterModComms.sendRuntimeMessage(
            UtilitiesInExcess.MODID,
            "NEIPlugins",
            "register-crafting-handler",
            "utilitiesinexcess@" + StatCollector.translateToLocal("nei.title.uie.pseudo_reversion")
                + "@pseudo_reversion_recipes");
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber(side = Side.CLIENT)
    public static class Events {

        @EventBusSubscriber.Condition
        public static boolean shouldSubscribe() {
            return Mods.NEI.isLoaded();
        }

        @SubscribeEvent
        public static void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
            event.registerHandlerInfo(
                new HandlerInfo.Builder("ender_locus_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                    .setHeight(140)
                    .setWidth(166)
                    .setDisplayStack(ModBlocks.ENDER_LOCUS.newItemStack())
                    .build());
            event.registerHandlerInfo(
                new HandlerInfo.Builder("pseudo_reversion_recipes", UtilitiesInExcess.MODNAME, UtilitiesInExcess.MODID)
                    .setHeight(140)
                    .setWidth(166)
                    .setDisplayStack(ModItems.PSEUDO_REVERSION_SIGIL.newItemStack())
                    .build());
        }
    }

    public static void registerAliases() {
        // spotless:off
        ArrayList<ItemStack> aliasedItemStacks = new ArrayList<>(List.of(
            new ItemStack(ModBlocks.FLOATING_BLOCK.getItem()),
            new ItemStack(ModBlocks.UNDERWORLD_PORTAL.getItem()),
            new ItemStack(ModBlocks.END_OF_TIME_PORTAL.getItem()),
            new ItemStack(ModBlocks.PACIFISTS_BENCH.getItem()),
            new ItemStack(ModBlocks.GIGA_TORCH.getItem()),
            new ItemStack(ModBlocks.ENDSPARK.getItem()),
            new ItemStack(ModBlocks.ENDER_LOCUS.getItem()),
            new ItemStack(ModBlocks.INVERTED_BLOCK.getItem()),
            new ItemStack(ModBlocks.MAGIC_WOOD.get()),
            new ItemStack(ModBlocks.SIGNIFICANTLY_SHRUNK_CHEST.get()),
            new ItemStack(ModBlocks.RADICALLY_REDUCED_CHEST.get()),
            new ItemStack(ModBlocks.MARGINALLY_MAXIMISED_CHEST.get()),
            new ItemStack(ModBlocks.LOW_TEMPERATURE_FURNACE_GENERATOR.get()),
            new ItemStack(ModBlocks.LOW_TEMPERATURE_FURNACE_SUBSTATION.get()),
            new ItemStack(ModBlocks.LOW_TEMPERATURE_FURNACE_POWERPLANT.get()),
            new ItemStack(ModBlocks.COLLECTOR.get()),

            new ItemStack(ModItems.ANTI_GRAVITY_SHOVEL.get()),
            new ItemStack(ModItems.MOB_JAR.get()),
            new ItemStack(ModItems.INVERSION_SIGIL_INACTIVE.get()),
            ItemInversionSigilActive.getStack(),
            new ItemStack(ModItems.PSEUDO_REVERSION_SIGIL.get()),
            new ItemStack(ModItems.INVERTED_INGOT.get()),
            new ItemStack(ModItems.INVERTED_INGOT.get(), 1, 1),
            new ItemStack(ModItems.ARCHITECTS_WAND.get()),
            new ItemStack(ModItems.SUPER_ARCHITECTS_WAND.get()),
            new ItemStack(ModItems.HEAVENLY_RING_FEATHER.get()),
            new ItemStack(ModItems.HEAVENLY_RING_DRAGON.get()),
            new ItemStack(ModItems.HEAVENLY_RING_FAIRY.get()),
            new ItemStack(ModItems.HEAVENLY_RING_METAL.get()),
            new ItemStack(ModItems.HEAVENLY_RING_MAGIC.get()),
            new ItemStack(ModItems.CHUNCHUNMARU.get()),
            new ItemStack(ModItems.FIRE_BATTERY.get()),
            new ItemStack(ModItems.ENDER_LOTUS_SEED.get()),
            new ItemStack(ModItems.BLOCK_ANALYZER.get()),
            new ItemStack(ModItems.XRAY_GLASSES.get())
        ));
        // spotless:on

        for (int i = 0; i < 16; i++) {
            aliasedItemStacks.add(new ItemStack(ModBlocks.LAPIS_AETHERIUS.getItem(), 1, i));
        }

        for (int i = 0; i < 10; i++) {
            aliasedItemStacks.add(new ItemStack(ModBlocks.DECORATIVE_GLASS.getItem(), 1, i));
        }

        for (ItemStack itemStack : aliasedItemStacks) {
            if (itemStack.getItem() == null) continue;

            API.setAliases(
                itemStack,
                StatCollector.translateToLocal(itemStack.getUnlocalizedName() + ".aliases")
                    .split(";"));
        }
    }
}
