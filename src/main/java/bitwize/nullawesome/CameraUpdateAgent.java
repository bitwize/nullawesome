package bitwize.nullawesome;

import android.util.Log;

public class CameraUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private int windowLeft, windowRight;

    public CameraUpdateAgent() {
	repo = EntityRepository.get();
	windowRight = DrawAgent.HRES * 6 / 10;
	windowLeft = DrawAgent.HRES * 4 / 10;
    }

    private void moveCamera(int stageEid, int playerEid) {
	SpriteMovement smov, pmov;
	smov = (SpriteMovement)repo.getComponent(stageEid, SpriteMovement.class);
	pmov = (SpriteMovement)repo.getComponent(playerEid, SpriteMovement.class);
	if(smov == null || pmov == null) return;
	if(pmov.position.x > smov.position.x + windowRight) {
	    smov.position.x = pmov.position.x - windowRight;
	}
	if(pmov.position.x < smov.position.x + windowLeft) {
	    smov.position.x = pmov.position.x - windowLeft;
	}
    }

    public void update(long time) {
	int stageEid = repo.findEntityWithComponent(StageInfo.class);
	int playerEid = repo.findEntityWithComponent(PlayerInfo.class);
	moveCamera(stageEid, playerEid);
    }
}
