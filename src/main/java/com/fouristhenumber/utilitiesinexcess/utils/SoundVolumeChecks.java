package com.fouristhenumber.utilitiesinexcess.utils;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.gtnewhorizon.gtnhlib.datastructs.space.ArrayProximityCheck4D;
import com.gtnewhorizon.gtnhlib.datastructs.space.VolumeShape;

public class SoundVolumeChecks {

    ArrayProximityCheck4D volumeCheckRain = new ArrayProximityCheck4D(VolumeShape.CUBE);
    ArrayProximityCheck4D volumeCheckSound = new ArrayProximityCheck4D(VolumeShape.CUBE);

    public void putSoundMuffler(int dim, int x, int y, int z) {
        volumeCheckSound.put(dim, x, y, z, BlockConfig.soundMuffler.soundMufflerRange);
    }

    public void removeSoundMuffler(int dim, int x, int y, int z) {
        volumeCheckSound.remove(dim, x, y, z);
    }

    public void putRainMuffler(int dim, int x, int y, int z) {
        volumeCheckRain.put(dim, x, y, z, BlockConfig.rainMuffler.rainMufflerRange);
    }

    public void removeRainMuffler(int dim, int x, int y, int z) {
        volumeCheckRain.remove(dim, x, y, z);
    }

    public boolean isInRainMufflerRange(int dim, double x, double y, double z) {
        return volumeCheckRain.isInRange(dim, x, y, z);
    }

    public boolean isInSoundMufflerRange(int dim, double x, double y, double z) {
        return volumeCheckSound.isInRange(dim, x, y, z);
    }
}
