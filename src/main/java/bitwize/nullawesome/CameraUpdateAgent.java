package bitwize.nullawesome;

import android.util.Log;

public class CameraUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private final int halfHres = DrawAgent.HRES / 2;
    public CameraUpdateAgent() {
        repo = EntityRepository.get();
    }

    private void moveCamera(int stageEid, int playerEid) {
        SpriteMovement smov, pmov;
        smov = (SpriteMovement)repo.getComponent(stageEid, SpriteMovement.class);
        pmov = (SpriteMovement)repo.getComponent(playerEid, SpriteMovement.class);
        if(smov == null || pmov == null) return;
        smov.position.x = pmov.position.x - halfHres;
        if(smov.position.x < 0) smov.position.x = 0;
    }

    public void update(long time) {
        int stageEid = repo.findEntityWithComponent(StageInfo.class);
        int playerEid = repo.findEntityWithComponent(PlayerInfo.class);
        moveCamera(stageEid, playerEid);
        StageInfo sinfo = (StageInfo)repo.getComponent(stageEid, StageInfo.class);
        sinfo.map.currentFrame = (short)((time >> 6) & 7);
    }
}
