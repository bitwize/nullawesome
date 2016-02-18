package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
public class SceneryDisplayAgent implements RenderAgent {
    public DrawAgent dagent;
    private EntityRepository repo;
    private EntityProcessor proc;
    private Point where;
    private Canvas cvs;
    public SceneryDisplayAgent(DrawAgent a) {
	dagent = a;
	repo = EntityRepository.get();
	where = new Point();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    try {
			StageInfo info = (StageInfo)repo.getComponent(eid, StageInfo.class);
			SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
			where.set((int)mv.position.x, (int)mv.position.y);
			dagent.drawMap(cvs, info.map, where);
		    } catch(InvalidEntityException e) {}
		}
	    };
    }
    public void drawOn(Canvas c) {
	cvs = c;
	repo.processEntitiesWithComponent(StageInfo.class, proc);
    }
}
