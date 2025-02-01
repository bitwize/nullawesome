package bitwize.nullawesome;

public class SoundUpdateAgent implements UpdateAgent {
    private SoundSystem ss;
    private int stageEid = EntityRepository.NO_ENTITY;
    public SoundUpdateAgent() {
        this.stageEid = stageEid;
        this.ss = new SoundSystem();
    }
    public void update(long time) {
        if(stageEid == EntityRepository.NO_ENTITY) {
            stageEid = EntityRepository.get().findEntityWithComponent(SoundInfo.class);
            if(stageEid == EntityRepository.NO_ENTITY) {
                return;
            }
        }
        SoundInfo si = (SoundInfo)(EntityRepository.get().getComponent(stageEid, SoundInfo.class));
        if(si == null) return;
        for(int i = 0; i < si.nsounds; i++) {
            ss.playSE(si.sounds[i], si.pans[i]);
        }
        si.clearSounds();
    }
}
