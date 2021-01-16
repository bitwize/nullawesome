package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
import java.util.ArrayList;
public class SpriteDisplayAgent implements RenderAgent {
    public DrawAgent dagent;
    private EntityRepository repo;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(SpriteShape.class));
    private PointF where;
    private PointF offsetWhere;
    private Canvas cvs;
    private static final int MAX_ZLAYERS = 4;
    private int[] zSorted = new int[EntityRepository.MAX_ENTITIES];
    private int[] zOrders = new int[EntityRepository.MAX_ENTITIES];
    private int zsTop = 0;
    private EntityProcessor zSort = (eid) -> {
	SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	if(mv == null) return;
	if((zsTop == 0) ||
	   (mv.zOrder >= zOrders[zsTop - 1])) {
	    zSorted[zsTop] = eid;
	    zOrders[zsTop] = mv.zOrder;
	} else {
	    int insertionPoint = 0;
	    for(int i=zsTop - 1; i>=0; i--) {
		if(mv.zOrder >= zOrders[i]) {
		    insertionPoint = i+1;
		    break;
		}
	    }
	    System.arraycopy(zSorted, insertionPoint, zSorted, insertionPoint + 1, (zsTop - insertionPoint));
	    System.arraycopy(zOrders, insertionPoint, zOrders, insertionPoint + 1, (zsTop - insertionPoint));
	    zSorted[insertionPoint] = eid;
	    zOrders[insertionPoint] = mv.zOrder;
	}
	zsTop++;
    };
    private EntityProcessor drawProc = (eid) -> {
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
    };
    public SpriteDisplayAgent(DrawAgent a) {
	dagent = a;
	repo = EntityRepository.get();
	where = new PointF();
	offsetWhere = new PointF();
    }
    public void drawOn(Canvas c) {
	cvs = c;
	zsTop = 0;
	repo.processEntitiesWithComponent(SpriteShape.class, zSort);
	for(int i = 0; i<zsTop; i++) {
	    drawProc.process(zSorted[i]);
	}
    }
}
