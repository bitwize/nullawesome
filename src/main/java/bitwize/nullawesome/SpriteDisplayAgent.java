package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
public class SpriteDisplayAgent implements RenderAgent {
    public DrawAgent dagent;
    private EntityRepository repo;
    private EntityProcessor proc;
    private PointF where;
    private Canvas cvs;
    public SpriteDisplayAgent(DrawAgent a) {
	dagent = a;
	repo = EntityRepository.get();
	where = new PointF();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    try {
			SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
			SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
			WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
			if(phys != null) {
			    SpriteMovement worldMv = (SpriteMovement)repo.getComponent(phys.stageEid, SpriteMovement.class);
			    where.set(worldMv.position);
			    where.negate();
			    where.offset(-(mv.hotspot.x), -(mv.hotspot.y));
			    where.offset(mv.position.x, mv.position.y);
			    dagent.drawSprite(cvs, shp.shapes, shp.subsection, where);
			}
			else
			{
			    dagent.drawSprite(cvs, shp.shapes, shp.subsection, mv.position);

			}
		    } catch(InvalidEntityException e) {}
		}
	    };
    }
    public void drawOn(Canvas c) {
	cvs = c;
	repo.processEntitiesWithComponent(SpriteShape.class, proc);
    }
}
