package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.PointF;
import java.util.EnumMap;

public class EnemyUpdateAgent implements UpdateAgent {
    private static EnumMap<EnemyType, Bitmap> enemyImagesL = new EnumMap<EnemyType, Bitmap>(EnemyType.class);
    private static EnumMap<EnemyType, Bitmap> enemyImagesR = new EnumMap<EnemyType, Bitmap>(EnemyType.class);
    private static final EnemyType[] enemyTypes = EnemyType.values();
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();
    private static PointF march = new PointF();
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(EnemyInfo.class));

    private EntityProcessor proc = (eid) -> {
	EnemyInfo ei = (EnemyInfo) repo.getComponent(eid, EnemyInfo.class);
	SpriteMovement mv = (SpriteMovement) repo.getComponent(eid, SpriteMovement.class);
	WorldPhysics phys = (WorldPhysics) repo.getComponent(eid, WorldPhysics.class);
	SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
	if(ei == null || mv == null || phys == null || shp == null) return;
	StageInfo info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
	if(info == null) return;
	TileMap map = info.map;
	if((phys.flags & WorldPhysics.FACING_RIGHT) != 0) {
	    shp.shapes = enemyImagesR.get(ei.type);
	} else {
	    shp.shapes = enemyImagesL.get(ei.type);
	}
	if(ei.targetEid != EntityRepository.NO_ENTITY) {
		SpriteMovement mvTarget = (SpriteMovement) repo.getComponent(ei.targetEid, SpriteMovement.class);
		if (mvTarget != null) {
		    boolean st = seesTarget(mv, mvTarget, map, ei.sightRange, ei.sightFrustum, (phys.flags & WorldPhysics.FACING_RIGHT) != 0, ei.flags, ei.currentState);
			if(st) {
				ei.flags |= EnemyInfo.SEES_TARGET;
			} else {
				ei.flags &= ~(EnemyInfo.SEES_TARGET);
			}
		}
	}
	EntityProcessor action = ei.stateActions.get(ei.currentState);
	if(action != null) {
	    action.process(eid);
	}
	EnemyStateTransition[] xions = ei.script.get(ei.currentState);
	if(xions != null) {
	    for(EnemyStateTransition xion : xions) {
		if(xion.criterion.test(eid)) {
		    ei.currentState = xion.newState;
		    break;
		}
	    }
	}
    };

    private static boolean seesTarget(SpriteMovement mvViewer, SpriteMovement mvTarget, TileMap map, float range, float frustAng, boolean facingRight, int flags, EnemyState state) {
	float distX = mvTarget.position.x - mvViewer.position.x;
	float distY = mvTarget.position.y - mvViewer.position.y;
	if(((distX * distX) + (distY * distY)) > range * range) {
		return false;
	}
	if((flags & EnemyInfo.LIDAR) == 0 &&
	   state != EnemyState.ATTACKING) {
	    if(facingRight && distX < 0) {
		return false;
	    }
	    if((!facingRight) && distX > 0) {
		return false;
	    }
	    if (Math.abs(Math.atan2(Math.abs(distY), Math.abs(distX))) > frustAng) {
		return false;
	    }
	}
	if((flags & EnemyInfo.IRVISION)==0) {
	    if(!hasLineOfSight(mvViewer.position, mvTarget.position, map)) {
		return false;
	    }
	}
	return true;
    }

    public static boolean hasLineOfSight(PointF start, PointF end, TileMap map) {
	march.set(start);
	float dx = end.x - start.x;
	float dy = end.y - start.y;
	float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
	dx *= TileMap.TILE_SIZE;
	dx /= dist;
	dy *= TileMap.TILE_SIZE;
	dy /= dist;
	for(float f=0; f<=dist; f += TileMap.TILE_SIZE) {
	    if((map.getTileFlags(map.getTileWorldCoords(march.x, march.y)) & TileMap.FLAG_SOLID) != 0) {
		return false;
	    }
	    march.x += dx;
	    march.y += dy;
	}
	return true;
    }

    public EnemyUpdateAgent() {
	for(EnemyType etype : enemyTypes) {
	    enemyImagesL.put(etype, content.getBitmap(etype.name().toLowerCase() + "_l"));
	    enemyImagesR.put(etype, content.getBitmap(etype.name().toLowerCase() + "_r"));
	}
	reh.register();
    }

    public void update(long time) {
	reh.processAll(proc);
    }    
}
