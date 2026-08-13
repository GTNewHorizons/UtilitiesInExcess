package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.AbstractUIFactory;
import com.cleanroommc.modularui.factory.GuiManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class PartGuiHandler extends AbstractUIFactory<PartGuiData>
{
    public static final PartGuiHandler INSTANCE = new PartGuiHandler("uie:part");

    protected PartGuiHandler(String name) {
        super(name);
    }

    public static void open(EntityPlayer player, TMultiPart part, int index) {
        if (part.tile() == null) {
            throw new IllegalArgumentException("Can't open invalid part GUI!");
        }
        if (player.worldObj != part.world()) {
            throw new IllegalArgumentException("Part must be in same dimension as the player!");
        }
        if (!(player instanceof EntityPlayerMP playerMP)) {
            throw new IllegalArgumentException("Part GUI must be opened on server side!");
        }
        if (player instanceof FakePlayer) {
            return;
        }
        PartGuiData data = new PartGuiData(player, part.x(), part.y(), part.z(), index);
        GuiManager.open(INSTANCE, data, playerMP);
    }

    @Override
    public @NotNull IGuiHolder<PartGuiData> getGuiHolder(PartGuiData data) {
        TileEntity te = data.getTileEntity();
        if (te instanceof TileMultipart multipartTE) {
            IGuiHolder<PartGuiData> guiHolder = castGuiHolder(multipartTE.partList().apply(data.partIndex));
            if (guiHolder != null) {
                return guiHolder;
            }
        }
        throw new IllegalStateException(
            String.format(
                "TileEntity at (%s, %s, %s) doesn't have referenced Part!",
                data.getX(),
                data.getY(),
                data.getZ()));
    }

    @Override
    public void writeGuiData(PartGuiData guiData, PacketBuffer buffer) {
        buffer.writeVarIntToBuffer(guiData.getX());
        buffer.writeVarIntToBuffer(guiData.getY());
        buffer.writeVarIntToBuffer(guiData.getZ());
        buffer.writeVarIntToBuffer(guiData.partIndex);
    }

    @Override
    public @NotNull PartGuiData readGuiData(EntityPlayer player, PacketBuffer buffer) {
        return new PartGuiData(
            player,
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer());
    }

    public static void init() {
        GuiManager.registerFactory(INSTANCE);
    }
}
