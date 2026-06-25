# Utilities In Excess<img src="img/logo_4x.png" align="right" alt="The Utilities in Excess logo">

An open source, cleanroom recreation of [Extra Utilities](https://www.curseforge.com/minecraft/mc-mods/extra-utilities)
for 1.7.10. Written entirely without viewing or reproducing the Extra Utilities source.

## Features

- The entire featureset of Extra Utilities.
- Replacements for integration features like Tinkers Construct parts.
- Better QoL: Empty fluid containers into a fluid trash can from hand! Blocks and items that simply work more smoothly.
- More mod integration: Spikes that work with modded enchantments, gloves that can be worn as a bauble,
[Backhand](https://github.com/GTNewHorizons/Backhand) support for Architect's Wands, and much more!
- New features: UiE contains a small number of new and expanded features, like the Radically Reduced Chest that holds
only a single item and dozens of new cosmetic options for the Heavenly Ring.
- Less bugs (we hope): Extra Utilities was left in a state where many of its features were completely broken. UiE has
aimed specifically to fix these and will receive ongoing support.
- Correct Documentation: Extra Utilities also has many completely wrong info pages that make certain features difficult
to understand. UiE also uses an NEI handler system, but the documentation is actually... not wrong.
- Extreme configurability. UiE is made for the entire 1.7.10 community and we are dedicated to supporting whatever
configurations you prefer or your pack requires.
- **And most importantly, FOSS now and forever!**

## World Conversion

Utilities in Excess features a full suite of [Postea](https://github.com/GTNewHorizons/Postea) transformers for
converting worlds with Extra Utilities blocks and items. These remappings happen seamlessly without any input necessary.

World conversion will happen automatically if Postea is installed, the config **#enableWorldConversion** is enabled
(on by default), and Extra Utilities is *not* installed.

Tinkers Construct conversion works by simply using the same material ids as Extra Utilities. If you want to run UiE and
ExU at the same time, you must change the configs **#bedrockiumTinkersID**, **#invertedTinkersID**,
and **#magicalWoodTinkersID** so that they no longer overlap. Otherwise, the game will crash!

## Hard Dependencies
- [GTNHLib](https://github.com/GTNewHorizons/GTNHLib)
- [ModularUI2](https://github.com/GTNewHorizons/ModularUI2)
- [CoFH Lib](https://www.curseforge.com/minecraft/mc-mods/cofh-lib)

## Optional Dependencies

- [Angelica](https://github.com/GTNewHorizons/Angelica) (CTM)
- [Postea](https://github.com/GTNewHorizons/Postea) (World conversion system)
- [Tinkers Construct](https://github.com/GTNewHorizons/TinkersConstruct) (New materials)
- [Forge Multipart](https://github.com/GTNewHorizons/ForgeMultipart) (New microblock shapes)
- [Backhand](https://github.com/GTNewHorizons/Backhand) (Architect's Wand integration)
- [FindIt](https://github.com/GTNewHorizons/FindIt) (Trading Post integration)

## Contribution

Utilities in Excess is under the GTNewHorizons organization and written with the express intent to support the GT:NH
modpack. **However,** support for external use has also been one of the core goals for the mod. Please feel free
to reach out if UiE isn't working for your modpack's use case, and to any GT:NH developers, if you find yourself asking
"Can I break external compatibility in order to..." the answer is **always no**.

Anyone is welcome to contribute to UiE, so please do not hesitate. If you write a new bugfix, mod integration, or
config, it will be almost certainly accepted after review. Please additionally review our
[AI usage policy](AI_POLICY.md) if you intend to use AI tooling.

If you want to add a new feature, please open an issue ticket first to request feedback. UiE is open to new features,
but they must fit the "vibe". A good fit for Utilities in Excess is a feature which:
- Is a bit of a standalone gimmick
- Works well alongside vanilla by itself
- Provides a useful ability hard to find in any other mod

## Credits

A special thanks to the 1.0 contributors, without which this project could have never gotten off the ground:

### Artists
<img src="src/main/resources/assets/utilitiesinexcess/textures/items/heavenly_ring_fairy.png" alt="Heavenly Ring"> &nbsp; &nbsp;  <img src="src/main/resources/assets/utilitiesinexcess/textures/blocks/temporal_gate_north.png" alt="Temporal Gate"> &nbsp; &nbsp;&nbsp;Embri <br>
<img src="img/drum.png" alt="Drum"> &nbsp; <img src="img/trash_can_item.png" alt="Trash Can"> &nbsp; EmeraldsEmerald <br>

### Programmers
- jurrejelle (Redstone Clock, Drums, Cursed Earth, Trash Cans, Architect's Wand)
- Caedis (Heavenly Ring, Ethereal Glass, Chandelier)
- KydZombie (Magic Wood, Inverted Tools)
- 0hwx (Watering Can, Filing Cabinet)
- dibbydoba (Pure Love, Special Chests, Sound/Rain Muffler)
- rspforhp (Inverted Tools)
- lynxx131 (Golden Bag of Holding, Block Update Detector)
- DeathFuel (Blackout Curtains)
- RecursivePineapple (Conveyors, The Underworld)
- serenibyss (QED)
- koolkrafter5 (End of Time)
- SuperSoupr (Glove, Blessed Earth, Colored Blocks, Trading Post, World Converter)
- loenaaaa (Generators, Decorative Blocks, Pseudo-Inversion Ritual)
- sisyphussy (Underworld Portal Renderer)
- Ranzuu (Localization Work)
- Cardinalstars (Forge Microblocks, Transfer Nodes)
- tomasz-brak (Architects Wand Backhand/GT5U Integration)
- Spicierspace153 (Collector)
- Malteez (Void Quarry)
- Spaghetti-OberNub (Bugfixes and Polish)
- Nikolay-Sitnikov (Code Review)
