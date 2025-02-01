package bitwize.nullawesome;

public class SoundInfo {
    public static final int MAXSOUNDS=10;
    public int nsounds = 0;
    public int[] sounds = new int[MAXSOUNDS];
    public float[] pans = new float[MAXSOUNDS];
    public void addSound(int snd, float pan) {
        if(nsounds >= MAXSOUNDS) {
            for(int i=1; i<MAXSOUNDS;i++) {
                sounds[i - 1] = sounds[i];
                pans[i - 1] = pans[i];
            }
            nsounds = MAXSOUNDS - 1;
        }
        sounds[nsounds] = snd;
        pans[nsounds] = pan;
        nsounds++;
    }
    public void clearSounds() {
        nsounds = 0;
    }
}
