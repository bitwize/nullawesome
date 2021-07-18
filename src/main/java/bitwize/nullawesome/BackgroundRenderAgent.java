package bitwize.nullawesome;

import android.graphics.*;

class BackgroundRenderAgent implements RenderAgent {
    public DrawAgent dagent;
    private int myEid;
    private PointF where = new PointF();
    public BackgroundRenderAgent(DrawAgent da, int eid) {
	dagent = da;
	myEid = eid;
    }
    public void draw() {
	EntityRepository repo = EntityRepository.get();
	StageInfo info = (StageInfo)repo.getComponent(myEid, StageInfo.class);
	SpriteMovement mv = (SpriteMovement)repo.getComponent(myEid, SpriteMovement.class);
	if(info == null || mv == null) return;
	where.set((mv.position.x * info.bgMoveScaleX), (mv.position.y * info.bgMoveScaleY));
	dagent.drawTileBG(info.map.getBackgroundImage(), info.map.getBackgroundImageSection(), where);
    }
}
