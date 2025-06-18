package com.fouristhenumber.utilitiesinexcess.utils;

import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
import com.gtnewhorizon.gtnhlib.datastructs.space.VolumeMembershipCheck;

public class SoundVolumeChecks {

    VolumeMembershipCheck volumeCheckRain = new VolumeMembershipCheck(VolumeMembershipCheck.VolumeShape.CUBE);
    VolumeMembershipCheck volumeCheckSound = new VolumeMembershipCheck(VolumeMembershipCheck.VolumeShape.CUBE);

    public void putSoundMuffler(int dim, int x, int y, int z) {
        volumeCheckSound.putVolume(dim, x, y, z, BlockConfig.soundMuffler.soundMufflerRange);
    }

    public void removeSoundMuffler(int dim, int x, int y, int z) {
        volumeCheckSound.removeVolume(dim, x, y, z);
    }

    public void putRainMuffler(int dim, int x, int y, int z) {
        volumeCheckRain.putVolume(dim, x, y, z, BlockConfig.rainMuffler.rainMufflerRange);
    }

    public void removeRainMuffler(int dim, int x, int y, int z) {
        volumeCheckRain.removeVolume(dim, x, y, z);
    }

    public boolean isInRainMufflerRange(int dim, double x, double y, double z) {
        return volumeCheckRain.isInVolume(dim, x, y, z);
    }

    public boolean isInSoundMufflerRange(int dim, double x, double y, double z) {
        return volumeCheckSound.isInVolume(dim, x, y, z);
    }
}
