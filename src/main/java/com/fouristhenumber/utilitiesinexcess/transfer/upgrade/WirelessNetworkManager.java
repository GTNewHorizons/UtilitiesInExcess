package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.HashMap;
import java.util.Map;

public class WirelessNetworkManager
{

    private static final Map<String, Int2ObjectOpenHashMap<LongOpenHashSet>> transmitters = new HashMap<>();
    private static final Int2ObjectOpenHashMap<Long2ObjectMap<String>> reverseTransmitterMap = new Int2ObjectOpenHashMap<>();

    private static final Map<String, Int2ObjectOpenHashMap<LongOpenHashSet>> receivers = new HashMap<>();
    private static final Int2ObjectOpenHashMap<Long2ObjectMap<String>> reverseReceiverMap = new Int2ObjectOpenHashMap<>();

    public static Int2ObjectOpenHashMap<LongOpenHashSet> getReceiversByFrequency(String frequency)
    {
        return receivers.get(frequency);
    }

    public static void registerTransmitter(String frequency, int dim, int x, int y, int z)
    {
        long packedCoords = CoordinatePacker.pack(x, y, z);
        transmitters
            .computeIfAbsent(frequency, f -> new Int2ObjectOpenHashMap<>())
            .computeIfAbsent(dim, d -> new LongOpenHashSet())
            .add(packedCoords);

        reverseTransmitterMap
            .computeIfAbsent(dim, d -> new Long2ObjectOpenHashMap<>())
            .put(packedCoords, frequency);
    }

    public static void deregisterTransmitter(String frequency, int dim, int x, int y, int z)
    {
        Int2ObjectOpenHashMap<LongOpenHashSet> byDim = transmitters.get(frequency);
        if (byDim != null)
        {
            LongOpenHashSet set = byDim.get(dim);
            if (set != null)
            {
                set.remove(CoordinatePacker.pack(x, y, z));
            }
        }
    }

    public static void deregisterTransmitter(int dim, int x, int y, int z)
    {
        long pos = CoordinatePacker.pack(x, y, z);

        Map<Long, String> lookup = reverseTransmitterMap.get(dim);
        if (lookup == null)
            return;

        String frequency = lookup.remove(pos);
        if (frequency == null)
            return;

        Map<Integer, LongOpenHashSet> byDim = transmitters.get(frequency);
        if (byDim == null)
            return;

        LongOpenHashSet nodes = byDim.get(dim);
        if (nodes != null)
        {
            nodes.remove(pos);

            if (nodes.isEmpty())
                byDim.remove(dim);
        }

        if (byDim.isEmpty())
            transmitters.remove(frequency);
    }

    public static void registerReceiver(String frequency, int dim, int x, int y, int z)
    {
        long packedCoords = CoordinatePacker.pack(x, y, z);
        receivers
            .computeIfAbsent(frequency, f -> new Int2ObjectOpenHashMap<>())
            .computeIfAbsent(dim, d -> new LongOpenHashSet())
            .add(packedCoords);

        reverseReceiverMap
            .computeIfAbsent(dim, d -> new Long2ObjectOpenHashMap<>())
            .put(packedCoords, frequency);
    }

    public static void deregisterReceiver(String frequency, int dim, int x, int y, int z)
    {
        Int2ObjectOpenHashMap<LongOpenHashSet> byDim = receivers.get(frequency);
        if (byDim != null)
        {
            LongOpenHashSet set = byDim.get(dim);
            if (set != null)
            {
                set.remove(CoordinatePacker.pack(x, y, z));
            }
        }
    }

    public static void deregisterReceiver(int dim, int x, int y, int z)
    {
        long pos = CoordinatePacker.pack(x, y, z);

        Map<Long, String> lookup = reverseReceiverMap.get(dim);
        if (lookup == null)
            return;

        String frequency = lookup.remove(pos);
        if (frequency == null)
            return;

        Map<Integer, LongOpenHashSet> byDim = receivers.get(frequency);
        if (byDim == null)
            return;

        LongOpenHashSet nodes = byDim.get(dim);
        if (nodes != null)
        {
            nodes.remove(pos);

            if (nodes.isEmpty())
                byDim.remove(dim);
        }

        if (byDim.isEmpty())
            receivers.remove(frequency);
    }
}
