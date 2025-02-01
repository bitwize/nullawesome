package bitwize.nullawesome;

import android.media.SoundPool;

public class SoundSystem {
    private SoundPool sp;
    private static float HRES = (float)DrawAgent.HRES;
    public SoundSystem() {
        sp = ContentRepository.get().getSoundPool();
    }
    public int playSE(int soundId, float xCoord) {
        return sp.play(soundId, (HRES - xCoord) / HRES, xCoord / HRES, 1, 0, 1.0f);
    }
    public int playSELooping(int soundId, float xCoord) {
        return sp.play(soundId, (HRES - xCoord) / HRES, xCoord / HRES, 1, -1, 1.0f);
    }
    public void stopSE(int streamId) {
        sp.stop(streamId);
    }
    public void pauseSE(int streamId) {
        sp.pause(streamId);
    }
}
