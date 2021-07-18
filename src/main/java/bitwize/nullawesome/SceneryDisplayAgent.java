package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
public class SceneryDisplayAgent implements RenderAgent {
    public DrawAgent dagent;
    private EntityProcessor proc;
    private EntityRepository repo;
    private Point where;
    private int myEid;
    public SceneryDisplayAgent(DrawAgent a,int eid) {
	dagent = a;
	repo = EntityRepository.get();
	where = new Point();
	myEid = eid;
	StageInfo info = (StageInfo)repo.getComponent(myEid, StageInfo.class);
	proc = new EntityProcessor() {
		public void process(int eid) {
		    StageInfo info = (StageInfo)repo.getComponent(eid, StageInfo.class);
		    SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		    if(info == null || mv == null) return;
		    where.set((int)mv.position.x, (int)mv.position.y);
		    dagent.drawMap(info.map, where);
		}
	    };
    }
    public void draw() {
	proc.process(myEid);
    }
}
