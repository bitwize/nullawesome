package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
public class SpriteDisplayAgent implements RenderAgent {
    public DrawAgent dagent;
    private EntityRepository repo;
    private EntityProcessor proc;
    private PointF where;
    private PointF offsetWhere;
    private Canvas cvs;
    public SpriteDisplayAgent(DrawAgent a) {
	dagent = a;
	repo = EntityRepository.get();
	where = new PointF();
	offsetWhere = new PointF();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
		    SpriteOverlay ovl = (SpriteOverlay)repo.getComponent(eid, SpriteOverlay.class);
		    SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		    WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
		    if(shp == null || mv == null || phys == null) return;
		    SpriteMovement worldMv = (SpriteMovement)repo.getComponent(phys.stageEid, SpriteMovement.class);
		    where.set(worldMv.position);
		    where.negate();
		    where.offset(mv.position.x, mv.position.y);
		    offsetWhere.set(where);
		    offsetWhere.offset(-(mv.hotspot.x), -(mv.hotspot.y));
		    dagent.drawSprite(cvs, shp.shapes, shp.subsection, offsetWhere);
		    if(ovl != null) {
			for (int i=0; i<ovl.shapes.length; i++) {
			    offsetWhere.set(where);
			    offsetWhere.offset(ovl.offsets[i].x, ovl.offsets[i].y);
			    if((!ovl.draw.get(i)) || (ovl.shapes[i] == null)) continue;
			    dagent.drawSprite(cvs, ovl.shapes[i].shapes, ovl.shapes[i].subsection, offsetWhere);
			}
		    }
		}
	    };
    }
    public void drawOn(Canvas c) {
	cvs = c;
	repo.processEntitiesWithComponent(SpriteShape.class, proc);
    }
}
