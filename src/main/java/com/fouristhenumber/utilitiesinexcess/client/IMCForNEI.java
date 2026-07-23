package com.fouristhenumber.utilitiesinexcess.client;

import net.minecraft.nbt.NBTTagCompound;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.config.blocks.ColoredBlocksConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;

import cpw.mods.fml.common.event.FMLInterModComms;

public class IMCForNEI {

    public static void IMCSender() {
        sendInfoPage(
            "utilitiesinexcess:compressed_cobblestone,utilitiesinexcess:compressed_dirt,utilitiesinexcess:compressed_gravel,utilitiesinexcess:compressed_sand",
            "uie.nei.infopage.compressed.1");

        sendInfoPage("<utilitiesinexcess:inverted_ingot:0>", "uie.nei.infopage.inverted_ingot.1");
        if (InversionConfig.INSTANCE.invertedIngotMode != InversionConfig.InversionMode.OFF) {
            sendInfoPage("<utilitiesinexcess:inverted_ingot:0>", "uie.nei.infopage.inverted_ingot.2");

            switch (InversionConfig.INSTANCE.invertedIngotMode) {
                case IMPLODE -> sendInfoPage(
                    "<utilitiesinexcess:inverted_ingot:0>",
                    "uie.nei.infopage.inverted_ingot.3.implode");
                case DECAY -> sendInfoPage(
                    "<utilitiesinexcess:inverted_ingot:0>",
                    "uie.nei.infopage.inverted_ingot.3.decay");
                case DISAPPEAR -> sendInfoPage(
                    "<utilitiesinexcess:inverted_ingot:0>",
                    "uie.nei.infopage.inverted_ingot.3.disappear");
            }
        }

        sendInfoPage(
            "<utilitiesinexcess:inverted_nugget>,<utilitiesinexcess:inverted_ingot:1>,<utilitiesinexcess:inverted_ingot:2>",
            "uie.nei.infopage.inverted_ingot_stable.1");
        sendInfoPage(
            "<utilitiesinexcess:inverted_nugget>,<utilitiesinexcess:inverted_ingot:1>,<utilitiesinexcess:inverted_ingot:2>",
            "uie.nei.infopage.inverted_ingot_stable.2");

        if (Mods.Tinkers.isLoaded()) {
            sendInfoPage("utilitiesinexcess:inverted_ingot", "uie.nei.infopage.ticon_inverted");
        }

        sendInfoPage("<utilitiesinexcess:sating_axe>", "uie.nei.infopage.sating_axe.1");
        sendInfoPage("<utilitiesinexcess:erasure_pickaxe>", "uie.nei.infopage.erasure_pickaxe.1");
        sendInfoPage("<utilitiesinexcess:anti_gravity_shovel>", "uie.nei.infopage.anti_gravity_shovel.1");
        sendInfoPage("<utilitiesinexcess:recall_shears>", "uie.nei.infopage.recall_shears.1");
        sendInfoPage("<utilitiesinexcess:liminal_sword>", "uie.nei.infopage.liminal_sword.1");
        sendInfoPage("<utilitiesinexcess:retrograde_hoe>", "uie.nei.infopage.retrograde_hoe.1");

        sendInfoPage(
            "utilitiesinexcess:filing_cabinet,<utilitiesinexcess:capacity_upgrade>",
            "uie.nei.infopage.filing_cabinet.1");
        sendInfoPage(
            "utilitiesinexcess:filing_cabinet,<utilitiesinexcess:capacity_upgrade>",
            "uie.nei.infopage.filing_cabinet.2");
        sendInfoPage(
            "utilitiesinexcess:filing_cabinet,<utilitiesinexcess:capacity_upgrade>",
            "uie.nei.infopage.filing_cabinet.3");
        sendInfoPage(
            "utilitiesinexcess:filing_cabinet,<utilitiesinexcess:capacity_upgrade>",
            "uie.nei.infopage.filing_cabinet.4");

        sendInfoPage(
            "<utilitiesinexcess:builders_wand>,<utilitiesinexcess:super_builders_wand>",
            "uie.nei.infopage.builders_wand.1");

        if (Mods.Backhand.isLoaded()) {
            sendInfoPage(
                "<utilitiesinexcess:builders_wand>,<utilitiesinexcess:super_builders_wand>",
                "uie.nei.infopage.builders_wand.2");
            if (Mods.GT.isLoaded()) sendInfoPage(
                "<utilitiesinexcess:builders_wand>,<utilitiesinexcess:super_builders_wand>",
                "uie.nei.infopage.builders_wand.3");
        }
        sendInfoPage("<utilitiesinexcess:mob_jar>", "uie.nei.infopage.mob_jar.1");

        sendInfoPage("utilitiesinexcess:glove", "uie.nei.infopage.glove.1");
        if (Mods.Baubles.isLoaded()) sendInfoPage("utilitiesinexcess:glove", "uie.nei.infopage.glove.2");

        sendInfoPage("<utilitiesinexcess:heavenly_block>", "uie.nei.infopage.heavenly_block.1");

        sendInfoPage("<utilitiesinexcess:redstone_clock>", "uie.nei.infopage.redstone_clock.1");

        sendInfoPage("<utilitiesinexcess:wood_spike>", "uie.nei.infopage.wood_spike.1");
        sendInfoPage("<utilitiesinexcess:iron_spike>", "uie.nei.infopage.iron_spike.1");
        sendInfoPage("<utilitiesinexcess:gold_spike>", "uie.nei.infopage.gold_spike.1");
        sendInfoPage("<utilitiesinexcess:diamond_spike>", "uie.nei.infopage.diamond_spike.1");

        sendInfoPage("<utilitiesinexcess:fire_battery>", "uie.nei.infopage.fire_battery.1");

        sendInfoPage("<utilitiesinexcess:block_analyzer>", "uie.nei.infopage.block_analyzer.1");

        sendInfoPage("<utilitiesinexcess:conveyor>", "uie.nei.infopage.conveyor.1");

        sendInfoPage("<utilitiesinexcess:marginally_maximised_chest>", "uie.nei.infopage.marginally_maximised_chest.1");
        sendInfoPage("<utilitiesinexcess:significantly_shrunk_chest>", "uie.nei.infopage.significantly_shrunk_chest.1");
        sendInfoPage("<utilitiesinexcess:radically_reduced_chest>", "uie.nei.infopage.radically_reduced_chest.1");

        sendInfoPage("<utilitiesinexcess:pure_love>", "uie.nei.infopage.pure_love.1");

        sendInfoPage("<utilitiesinexcess:drum>", "uie.nei.infopage.drum.1");

        sendInfoPage(
            "<utilitiesinexcess:ender_locus>,<utilitiesinexcess:convergence_crystal>",
            "uie.nei.infopage.ender_locus.1");

        sendInfoPage("<utilitiesinexcess:block_update_detector>", "uie.nei.infopage.block_update_detector.1");
        sendInfoPage(
            "<utilitiesinexcess:advanced_block_update_detector>",
            "uie.nei.infopage.advanced.block_update_detector.1");

        sendInfoPage("<utilitiesinexcess:rain_muffler>", "uie.nei.infopage.rain_muffler.1");
        sendInfoPage("<utilitiesinexcess:sound_muffler>", "uie.nei.infopage.sound_muffler.1");

        sendInfoPage(
            "<utilitiesinexcess:bedrockium_ingot>,<utilitiesinexcess:bedrockium_block>",
            "uie.nei.infopage.bedrockium.1");
        if (Mods.Tinkers.isLoaded()) {
            sendInfoPage("<utilitiesinexcess:bedrockium_ingot>", "uie.nei.infopage.ticon_bedrockium");
        }

        sendInfoPage(
            "<utilitiesinexcess:watering_can_basic>,<utilitiesinexcess:watering_can_advanced>,<utilitiesinexcess:watering_can_elite>",
            "uie.nei.infopage.watering_can.1");

        if (Mods.Baubles.isLoaded()) {
            sendInfoPage(
                "<utilitiesinexcess:heavenly_ring_feather>,<utilitiesinexcess:heavenly_ring_dragon>,<utilitiesinexcess:heavenly_ring_fairy>,<utilitiesinexcess:heavenly_ring_metal>,<utilitiesinexcess:heavenly_ring_magic>",
                "uie.nei.infopage.heavenly_ring.2");
        } else {
            sendInfoPage(
                "<utilitiesinexcess:heavenly_ring_feather>,<utilitiesinexcess:heavenly_ring_dragon>,<utilitiesinexcess:heavenly_ring_fairy>,<utilitiesinexcess:heavenly_ring_metal>,<utilitiesinexcess:heavenly_ring_magic>",
                "uie.nei.infopage.heavenly_ring.1");
        }

        sendInfoPage("<utilitiesinexcess:trash_can_item>", "uie.nei.infopage.trash_can_item.1");
        sendInfoPage("<utilitiesinexcess:trash_can_fluid>", "uie.nei.infopage.trash_can_fluid.1");
        sendInfoPage("<utilitiesinexcess:trash_can_energy>", "uie.nei.infopage.trash_can_energy.1");

        sendInfoPage("<utilitiesinexcess:magic_wood>", "uie.nei.infopage.magic_wood.1");
        if (Mods.Tinkers.isLoaded()) {
            sendInfoPage("<utilitiesinexcess:magic_wood>", "uie.nei.infopage.ticon_magic_wood");
        }

        sendInfoPage("<utilitiesinexcess:endspark>", "uie.nei.infopage.endspark.1");

        sendInfoPage("<utilitiesinexcess:pacifists_bench>", "uie.nei.infopage.pacifists_bench.1");
        sendInfoPage("<utilitiesinexcess:pacifists_bench>", "uie.nei.infopage.pacifists_bench.2");

        sendInfoPage("<utilitiesinexcess:smart_pump>", "uie.nei.infopage.smart_pump.1");
        sendInfoPage("<utilitiesinexcess:smart_pump>", "uie.nei.infopage.smart_pump.2");

        sendInfoPage("<utilitiesinexcess:ender_lotus_seed>", "uie.nei.infopage.ender_lotus_seed.1");

        sendInfoPage("<utilitiesinexcess:xray_glasses>", "uie.nei.infopage.xray_glasses.1");

        sendInfoPage("<utilitiesinexcess:golden_bag>", "uie.nei.infopage.golden_bag.1");

        sendInfoPage(
            "<utilitiesinexcess:low_temperature_furnace_generator>,<utilitiesinexcess:low_temperature_furnace_substation>,<utilitiesinexcess:low_temperature_furnace_powerplant>",
            "uie.nei.infopage.low_temperature_furnace_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:furnace_generator>,<utilitiesinexcess:furnace_substation>,<utilitiesinexcess:furnace_powerplant>",
            "uie.nei.infopage.furnace_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:high_temperature_furnace_generator>,<utilitiesinexcess:high_temperature_furnace_substation>,<utilitiesinexcess:high_temperature_furnace_powerplant>",
            "uie.nei.infopage.high_temperature_furnace_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:ender_generator>,<utilitiesinexcess:ender_substation>,<utilitiesinexcess:ender_powerplant>",
            "uie.nei.infopage.ender_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:food_generator>,<utilitiesinexcess:food_substation>,<utilitiesinexcess:food_powerplant>",
            "uie.nei.infopage.food_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:tnt_generator>,<utilitiesinexcess:tnt_substation>,<utilitiesinexcess:tnt_powerplant>",
            "uie.nei.infopage.tnt_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:nether_star_generator>,<utilitiesinexcess:nether_star_substation>,<utilitiesinexcess:nether_star_powerplant>",
            "uie.nei.infopage.nether_star_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:pink_generator>,<utilitiesinexcess:pink_substation>,<utilitiesinexcess:pink_powerplant>",
            "uie.nei.infopage.pink_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:lava_generator>,<utilitiesinexcess:lava_substation>,<utilitiesinexcess:lava_powerplant>",
            "uie.nei.infopage.lava_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:solar_generator>,<utilitiesinexcess:solar_substation>,<utilitiesinexcess:solar_powerplant>",
            "uie.nei.infopage.solar_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:redstone_generator>,<utilitiesinexcess:redstone_substation>,<utilitiesinexcess:redstone_powerplant>",
            "uie.nei.infopage.redstone_generator.1");
        sendInfoPage(
            "<utilitiesinexcess:potion_generator>,<utilitiesinexcess:potion_substation>,<utilitiesinexcess:potion_powerplant>",
            "uie.nei.infopage.potion_generator.1");

        sendInfoPage("<utilitiesinexcess:temporal_gate>", "uie.nei.infopage.temporal_gate.1");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "uie.nei.infopage.temporal_gate.2");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "uie.nei.infopage.temporal_gate.3");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "uie.nei.infopage.temporal_gate.4");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "uie.nei.infopage.temporal_gate.5");

        sendInfoPage(
            "<utilitiesinexcess:ethereal_glass>,<utilitiesinexcess:ethereal_glass:1>",
            "uie.nei.infopage.ethereal_glass.0");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:2>", "uie.nei.infopage.ethereal_glass.2");
        sendInfoPage(
            "<utilitiesinexcess:ethereal_glass:3>,<utilitiesinexcess:ethereal_glass:4>",
            "uie.nei.infopage.ethereal_glass.3");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:5>", "uie.nei.infopage.ethereal_glass.5");

        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "uie.nei.infopage.inversion_sigil.1");
        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "uie.nei.infopage.inversion_sigil.2");
        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "uie.nei.infopage.inversion_sigil.3");
        sendInfoPage(
            "<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>",
            "uie.nei.infopage.inversion_sigil.4");

        sendInfoPage("<utilitiesinexcess:pseudo_reversion_sigil>", "uie.nei.infopage.pseudo_reversion_sigil.1");
        sendInfoPage("<utilitiesinexcess:pseudo_reversion_sigil>", "uie.nei.infopage.pseudo_reversion_sigil.2");
        sendInfoPage("<utilitiesinexcess:pseudo_reversion_sigil>", "uie.nei.infopage.pseudo_reversion_sigil.3");

        sendInfoPage("<utilitiesinexcess:cursed_earth>", "uie.nei.infopage.cursed_earth.1");

        sendInfoPage("<utilitiesinexcess:blessed_earth>", "uie.nei.infopage.blessed_earth.1");

        sendInfoPage("utilitiesinexcess:lapis_aetherius", "uie.nei.infopage.lapis_aetherius.1");
        if (BlockColored.allowDyingBlocks())
            sendInfoPage("utilitiesinexcess:lapis_aetherius", "uie.nei.infopage.lapis_aetherius.2");

        sendInfoPage("utilitiesinexcess:collector", "uie.nei.infopage.collector.1");

        sendInfoPage("<utilitiesinexcess:blackout_curtains>", "uie.nei.infopage.blackout_curtains.1");

        sendInfoPage("<utilitiesinexcess:underworld_portal>", "uie.nei.infopage.underworld_portal.1");
        sendInfoPage("<utilitiesinexcess:underworld_portal>", "uie.nei.infopage.underworld_portal.2");
        sendInfoPage("<utilitiesinexcess:underworld_portal>", "uie.nei.infopage.underworld_portal.3");

        sendInfoPage("utilitiesinexcess:chandelier", "uie.nei.infopage.chandelier.1");

        sendInfoPage("<utilitiesinexcess:giga_torch>", "uie.nei.infopage.giga_torch.1");

        sendInfoPage("<utilitiesinexcess:trading_post>", "uie.nei.infopage.trading_post.1");

        sendInfoPage("<utilitiesinexcess:void_quarry>", "uie.nei.infopage.void_quarry.1");
        sendInfoPage("<utilitiesinexcess:void_quarry>", "uie.nei.infopage.void_quarry.2");
        sendInfoPage("<utilitiesinexcess:void_quarry>", "uie.nei.infopage.void_quarry.3");

        sendInfoPage("<utilitiesinexcess:void_marker>", "uie.nei.infopage.void_marker.1");
        sendInfoPage("<utilitiesinexcess:void_marker>", "uie.nei.infopage.void_marker.2");
        sendInfoPage("<utilitiesinexcess:void_marker>", "uie.nei.infopage.void_marker.3");

        sendInfoPage(
            "<utilitiesinexcess:void_quarry>,utilitiesinexcess:void_quarry_upgrade",
            "uie.nei.infopage.void_quarry_upgrade");
        sendInfoPage("<utilitiesinexcess:void_quarry_upgrade:0>", "uie.nei.infopage.void_quarry_upgrade.world_hole");
        sendInfoPage("<utilitiesinexcess:void_quarry_upgrade:1>", "uie.nei.infopage.void_quarry_upgrade.silk_touch");
        sendInfoPage("<utilitiesinexcess:void_quarry_upgrade:2>", "uie.nei.infopage.void_quarry_upgrade.fluid_pump");
        sendInfoPage(
            "<utilitiesinexcess:void_quarry_upgrade:3>,<utilitiesinexcess:void_quarry_upgrade:4>,<utilitiesinexcess:void_quarry_upgrade:5>",
            "uie.nei.infopage.void_quarry_upgrade.speed");
        sendInfoPage(
            "<utilitiesinexcess:void_quarry_upgrade:6>,<utilitiesinexcess:void_quarry_upgrade:7>,<utilitiesinexcess:void_quarry_upgrade:8>",
            "uie.nei.infopage.void_quarry_upgrade.fortune");

        sendInfoPage("<utilitiesinexcess:true_greenscreen>", "uie.nei.infopage.true_greenscreen.0");
        sendInfoPage("<utilitiesinexcess:true_greenscreen>", "uie.nei.infopage.true_greenscreen.1");

        if (ColoredBlocksConfig.INSTANCE.enableColoredBlocks && BlockColored.allowDyingBlocks()) {
            sendInfoPage("<utilitiesinexcess:paint_roller>", "uie.nei.infopage.paint_roller");

            for (BlockColored block : BlockColored.COLORED_BLOCKS) {
                if (block.useDefaultNEIPage()) {
                    sendInfoPage(
                        "utilitiesinexcess:" + block.getRegistryName(),
                        "uie.nei.infopage.colored_blocks.dyeable");
                }
                if (block.getCustomNEIPage() != null) {
                    sendInfoPage("utilitiesinexcess:" + block.getRegistryName(), block.getCustomNEIPage());
                }
            }
        }
    }

    private static void sendInfoPage(String filter, String page) {
        NBTTagCompound NBT = new NBTTagCompound();
        NBT.setString("filter", filter);
        NBT.setString("page", page);
        FMLInterModComms.sendMessage("NotEnoughItems", "addItemInfo", NBT);
    }
}
