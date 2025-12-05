package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.lwjgl.input.Keyboard;

import com.fouristhenumber.utilitiesinexcess.client.IMCForNEI;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.EndOfTimeEvents;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.UnderWorldEvents;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.exu.Remappings;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.utils.SoundVolumeChecks;
import com.gtnewhorizon.gtnhlib.keybind.SyncedKeybind;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public SoundVolumeChecks soundVolumeChecks;

    public SyncedKeybind GLOVE_KEYBIND;

    public void preInit(FMLPreInitializationEvent event) {
        // Config is handled in the early mixin loader (UIEMixinLoader)
        // since we want the config to be available
        // during mixin initialisation time.
        PacketHandler.init();
        ModBlocks.init();
        ModItems.init();
        ModDimensions.init();
        ModBiomes.init();
        UnderWorldEvents.init();
        EndOfTimeEvents.init();
        if (Mods.NEI.isLoaded()) {
            IMCForNEI.IMCSender();
        }
        Remappings.init();
        if (!Mods.ExtraUtilities.isLoaded()) {
            for (IPosteaTransformation transformation : Remappings.transformations) {
                transformation.registerDummies();
            }
        }
        Remappings.initTransformationMappings();
    }

    public void init(FMLInitializationEvent event) {
        soundVolumeChecks = new SoundVolumeChecks();
        GLOVE_KEYBIND = SyncedKeybind.createConfigurable("key.uie.glove", "key.categories.uie", Keyboard.KEY_NONE);
    }

    public void postInit(FMLPostInitializationEvent event) {
        for (IPosteaTransformation transformation : Remappings.transformations) {
            transformation.registerTransformations();
        }
        TileEntityReplacementManager.tileEntityTransformer("Furnace", (tag, world) -> {
            return new BlockInfo(Blocks.chest, 0, (oldTag) -> {

                NBTTagCompound newTag = PosteaUtilities.cleanseNBT("Chest", oldTag);

                NBTTagList tagList = new NBTTagList();

                NBTTagCompound stoneAtSlot13 = new NBTTagCompound();
                stoneAtSlot13.setByte("Count", (byte) 1);
                stoneAtSlot13.setByte("Slot", (byte) 13);
                stoneAtSlot13.setShort("Damage", (short) 0);
                stoneAtSlot13.setShort("id", (short) Block.getIdFromBlock(Blocks.stone));

                tagList.appendTag(stoneAtSlot13);

                newTag.setTag("Items", tagList);
                newTag.setInteger("banana", 1);

                return newTag;
            });
        });
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
