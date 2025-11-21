package com.fouristhenumber.utilitiesinexcess.client;

import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

import cpw.mods.fml.common.event.FMLInterModComms;

public class IMCForNEI {

    public static void IMCSender() {
        sendInfoPage(
            "utilitiesinexcess:compressed_cobblestone,utilitiesinexcess:compressed_dirt,utilitiesinexcess:compressed_gravel,utilitiesinexcess:compressed_sand",
            "nei.infopage.uie.compressed.1");

        sendInfoPage("<utilitiesinexcess:inverted_ingot:0>", "nei.infopage.uie.inverted_ingot.1");
        if (InversionConfig.invertedIngotsImplode) {
            sendInfoPage("<utilitiesinexcess:inverted_ingot:0>", "nei.infopage.uie.inverted_ingot.2");
            sendInfoPage("<utilitiesinexcess:inverted_ingot:0>", "nei.infopage.uie.inverted_ingot.3");
        }

        sendInfoPage(
            "<utilitiesinexcess:inverted_ingot:1>,<utilitiesinexcess:inverted_nugget>",
            "nei.infopage.uie.inverted_ingot_stable.1");
        sendInfoPage(
            "<utilitiesinexcess:inverted_ingot:1>,<utilitiesinexcess:inverted_nugget>",
            "nei.infopage.uie.inverted_ingot_stable.2");

        sendInfoPage("<utilitiesinexcess:gluttons_axe>", "nei.infopage.uie.gluttons_axe.1");
        sendInfoPage("<utilitiesinexcess:destruction_pickaxe>", "nei.infopage.uie.destruction_pickaxe.1");
        sendInfoPage("<utilitiesinexcess:anti_particulate_shovel>", "nei.infopage.uie.anti_particulate_shovel.1");
        sendInfoPage("<utilitiesinexcess:precision_shears>", "nei.infopage.uie.precision_shears.1");
        sendInfoPage("<utilitiesinexcess:etheric_sword>", "nei.infopage.uie.etheric_sword.1");
        sendInfoPage("<utilitiesinexcess:reversing_hoe>", "nei.infopage.uie.reversing_hoe.1");
        sendInfoPage(
            "<utilitiesinexcess:architectsWand>,<utilitiesinexcess:superArchitectsWand>",
            "nei.infopage.uie.architects_wand.1");

        sendInfoPage("<utilitiesinexcess:mob_jar>", "nei.infopage.uie.mob_jar.1");

        sendInfoPage("<utilitiesinexcess:floating_block>", "nei.infopage.uie.floating_block.1");

        sendInfoPage("<utilitiesinexcess:redstone_clock>", "nei.infopage.uie.redstone_clock.1");

        sendInfoPage("<utilitiesinexcess:woodSpike>", "nei.infopage.uie.woodSpike.1");
        sendInfoPage("<utilitiesinexcess:ironSpike>", "nei.infopage.uie.ironSpike.1");
        sendInfoPage("<utilitiesinexcess:goldSpike>", "nei.infopage.uie.goldSpike.1");
        sendInfoPage("<utilitiesinexcess:diamondSpike>", "nei.infopage.uie.diamondSpike.1");

        sendInfoPage("<utilitiesinexcess:fire_battery>", "nei.infopage.uie.fire_battery.1");

        sendInfoPage("<utilitiesinexcess:block_analyzer>", "nei.infopage.uie.block_analyzer.1");

        sendInfoPage("<utilitiesinexcess:conveyor>", "nei.infopage.uie.conveyor.1");

        sendInfoPage("<utilitiesinexcess:marginally_maximised_chest>", "nei.infopage.uie.marginally_maximised_chest.1");
        sendInfoPage("<utilitiesinexcess:significantly_shrunk_chest>", "nei.infopage.uie.significantly_shrunk_chest.1");
        sendInfoPage("<utilitiesinexcess:radically_reduced_chest>", "nei.infopage.uie.radically_reduced_chest.1");

        sendInfoPage("<utilitiesinexcess:pure_love>", "nei.infopage.uie.pure_love.1");

        sendInfoPage("<utilitiesinexcess:drum>", "nei.infopage.uie.drum.1");

        sendInfoPage("<utilitiesinexcess:block_update_detector>", "nei.infopage.uie.block_update_detector.1");

        sendInfoPage("<utilitiesinexcess:rain_muffler>", "nei.infopage.uie.rain_muffler.1");
        sendInfoPage("<utilitiesinexcess:sound_muffler>", "nei.infopage.uie.sound_muffler.1");

        sendInfoPage(
            "<utilitiesinexcess:bedrockium_ingot>,<utilitiesinexcess:bedrockium_block>",
            "nei.infopage.uie.bedrockium.1");

        sendInfoPage(
            "<utilitiesinexcess:watering_can_basic>,<utilitiesinexcess:watering_can_advanced>,<utilitiesinexcess:watering_can_elite>",
            "nei.infopage.uie.watering_can.1");

        sendInfoPage("<utilitiesinexcess:heavenly_ring>", "nei.infopage.uie.heavenly_ring.1");

        sendInfoPage("<utilitiesinexcess:trash_can_item>", "nei.infopage.uie.trash_can_item.1");
        sendInfoPage("<utilitiesinexcess:trash_can_fluid>", "nei.infopage.uie.trash_can_fluid.1");
        sendInfoPage("<utilitiesinexcess:trash_can_energy>", "nei.infopage.uie.trash_can_energy.1");

        sendInfoPage("<utilitiesinexcess:magic_wood>", "nei.infopage.uie.magic_wood.1");

        sendInfoPage("<utilitiesinexcess:ender_lotus_seed>", "nei.infopage.uie.ender_lotus_seed.1");

        sendInfoPage("<utilitiesinexcess:xray_glasses>", "nei.infopage.uie.xray_glasses.1");

        sendInfoPage("<utilitiesinexcess:golden_bag>", "nei.infopage.uie.golden_bag.1");

        sendInfoPage(
            "<utilitiesinexcess:low_temperature_furnace_generator>,<utilitiesinexcess:low_temperature_furnace_generator_plus>,<utilitiesinexcess:low_temperature_furnace_generator_plusplus>",
            "nei.infopage.uie.low_temperature_furnace_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:furnace_generator>,<utilitiesinexcess:furnace_generator_plus>,<utilitiesinexcess:furnace_generator_plusplus>",
            "nei.infopage.uie.furnace_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:high_temperature_furnace_generator>,<utilitiesinexcess:high_temperature_furnace_generator_plus>,<utilitiesinexcess:high_temperature_furnace_generator_plusplus>",
            "nei.infopage.uie.high_temperature_furnace_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:ender_generator>,<utilitiesinexcess:ender_generator_plus>,<utilitiesinexcess:ender_generator_plusplus>",
            "nei.infopage.uie.ender_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:food_generator>,<utilitiesinexcess:food_generator_plus>,<utilitiesinexcess:food_generator_plusplus>",
            "nei.infopage.uie.food_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:tnt_generator>,<utilitiesinexcess:tnt_generator_plus>,<utilitiesinexcess:tnt_generator_plusplus>",
            "nei.infopage.uie.tnt_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:nether_star_generator>,<utilitiesinexcess:nether_star_generator_plus>,<utilitiesinexcess:nether_star_generator_plusplus>",
            "nei.infopage.uie.nether_star_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:pink_generator>,<utilitiesinexcess:pink_generator_plus>,<utilitiesinexcess:pink_generator_plusplus>",
            "nei.infopage.uie.pink_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:lava_generator>,<utilitiesinexcess:lava_generator_plus>,<utilitiesinexcess:lava_generator_plusplus>",
            "nei.infopage.uie.lava_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:solar_generator>,<utilitiesinexcess:solar_generator_plus>,<utilitiesinexcess:solar_generator_plusplus>",
            "nei.infopage.uie.solar_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:redstone_generator>,<utilitiesinexcess:redstone_generator_plus>,<utilitiesinexcess:redstone_generator_plusplus>",
            "nei.infopage.uie.redstone_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:potion_generator>,<utilitiesinexcess:potion_generator_plus>,<utilitiesinexcess:potion_generator_plusplus>",
            "nei.infopage.uie.potion_generator.1");

        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.1");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.2");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.3");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.4");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.5");

        sendInfoPage("<utilitiesinexcess:ethereal_glass>", "nei.infopage.uie.ethereal_glass.0");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:2>", "nei.infopage.uie.ethereal_glass.2");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:3>", "nei.infopage.uie.ethereal_glass.3");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:5>", "nei.infopage.uie.ethereal_glass.5");

        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "nei.infopage.uie.inversion_sigil.1");
        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "nei.infopage.uie.inversion_sigil.2");
        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "nei.infopage.uie.inversion_sigil.3");
        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "nei.infopage.uie.inversion_sigil.4");

        sendInfoPage("<utilitiesinexcess:cursed_earth>", "nei.infopage.uie.cursed_earth.1");

        sendInfoPage("utilitiesinexcess:lapis_aetherius", "nei.infopage.uie.lapis_aetherius.1");

        sendInfoPage("<utilitiesinexcess:blackout_curtains>", "nei.infopage.uie.blackout_curtains.1");

        sendInfoPage("<utilitiesinexcess:underworld_portal>", "nei.infopage.uie.underworld_portal.1");
        sendInfoPage("<utilitiesinexcess:underworld_portal>", "nei.infopage.uie.underworld_portal.2");
        sendInfoPage("<utilitiesinexcess:underworld_portal>", "nei.infopage.uie.underworld_portal.3");
    }

    private static void sendInfoPage(String filter, String page) {
        NBTTagCompound NBT = new NBTTagCompound();
        NBT.setString("filter", filter);
        NBT.setString("page", page);
        FMLInterModComms.sendMessage("NotEnoughItems", "addItemInfo", NBT);
    }
}
