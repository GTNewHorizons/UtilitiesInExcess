# Utilities In Excess

An open source, cleanroom recreation of [Extra Utilities](https://www.curseforge.com/minecraft/mc-mods/extra-utilities)
for 1.7.10. Written entirely without viewing or reproducing the Extra Utilities source.

Optionally dependent on [Angelica](https://github.com/GTNewHorizons/Angelica) for connected texture mapping.

## Features

- The entire featureset of Extra Utilities.
- Replacements for integration features like Tinkers Construct parts.
- Better QoL: Empty fluid containers into a fluid trash can from hand! Blocks and items that simply work more smoothly.
- More mod integration: Spikes that work with modded enchantments, gloves that can be worn as a bauble,
[Backhand](https://github.com/GTNewHorizons/Backhand) support for Architect's Wands, and much more!
- New features: UiE contains a small number of new and expanded features, like the Radically Reduced Chest that holds
only a single item, or dozens of new cosmetic options for the Heavenly Ring.
- Less bugs (we hope): Extra Utilities was left in a state where many of its features were completely broken. UiE has
aimed specifically to fix these and will receive ongoing support.
- Extreme configurability. UiE is made for the entire 1.7.10 community and we are dedicated to supporting whatever
configurations you prefer or your pack requires.
- **And most importantly, FOSS now and forever!**

## World Conversion

Utilities in Excess features a full suite of [Postea](https://github.com/GTNewHorizons/Postea) transformers for
converting worlds with Extra Utilities blocks and items. These remappings happen seamlessly without any input necessary.

World conversion will happen automatically if Postea is installed, the config **#enableWorldConversion** is enabled
(on by default), and Extra Utilities is *not* installed.

Tinkers Construct conversion works by simply using the same material ids as Extra Utilities. If you want to run UiE and
ExU at the same time, you must change the configs **#invertedTinkersID** and **#magicalWoodTinkersID** so that they no
longer overlap. Otherwise, the game will crash!

## Contribution

Utilities in Excess is under the GTNewHorizons organization and written with the express intent to support the GT:NH
modpack. **However,** support for external use has also been one of the core goals for the mod. Please feel free
to reach out if UiE isn't working for your modpack's use case, and to any GT:NH developers, if you find yourself asking
"Can I break external compatibility in order to..." the answer is **always no**.

Anyone is welcome to contribute to UiE, so please do not hesitate. If you write a new bugfix, mod integration, or
config, it will be almost certainly accepted after review.

If you want to add a new feature, please open an issue ticket first to request feedback. UiE is open to new features,
but they must fit the "vibe". A good fit for Utilities in Excess is a feature which:
- Is a bit of a standalone gimmick
- Works well alongside vanilla by itself
- Provides a useful ability hard to find in any other mod
