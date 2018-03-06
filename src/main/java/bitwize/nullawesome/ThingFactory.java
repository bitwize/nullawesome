package bitwize.nullawesome;

import android.graphics.PointF;
import android.graphics.Rect;

public class ThingFactory {
    private static EntityProcessor terminalNodeAction = new EntityProcessor() {
	    public void process(int eid) {
		SpriteOverlay ovl = (SpriteOverlay)EntityRepository.get().getComponent(eid, SpriteOverlay.class);
		ovl.draw.set(0, true);
	    }
	};
    public int createTerminalNode(int stageEid, PointF location)
	throws EntityTableFullException {
	EntityRepository repo = EntityRepository.get();
	int eid = repo.newEntity();
	SpriteShape shp = new SpriteShape();
	shp.shapes = ContentRepository.get().getBitmap("terminal");
	SpriteShape shp2 = new SpriteShape();
	shp2.shapes = ContentRepository.get().getBitmap("terminal_greentext");
	shp.subsection = new Rect(0, 0, 19, 32);
	shp2.subsection = new Rect(0, 0, 9, 7);
	SpriteMovement mv = new SpriteMovement();
	WorldPhysics phys = new WorldPhysics();
	SpriteOverlay ovl = new SpriteOverlay(1)
	    .put(0, shp2, -1.f, -14.f);
	ovl.draw.set(0, false);
	mv.position.set(location.x, location.y);
	mv.hotspot.set(10, 16);
	HackTarget ht = new HackTarget();
	ht.width = 48;
	ht.height = 48;
	ht.action = terminalNodeAction;
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.GROUNDED;
	phys.gvelmax = 2.f;
	phys.radius = 16;
	phys.hitbox.left = -10;
	phys.hitbox.top = -16;
	phys.hitbox.right = 9;
	phys.hitbox.bottom = 16;
	repo.addComponent(eid, shp);
	repo.addComponent(eid, mv);
	repo.addComponent(eid, ht);
	repo.addComponent(eid, phys);
	repo.addComponent(eid, ovl);
	return eid;
    }
}
