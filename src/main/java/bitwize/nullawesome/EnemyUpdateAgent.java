package bitwize.nullawesome;

import android.graphics.Bitmap;
import java.util.EnumMap;

public class EnemyUpdateAgent implements UpdateAgent {
    private static EnumMap<EnemyType, Bitmap> enemyImagesL;
    private static EnumMap<EnemyType, Bitmap> enemyImagesR;
    private static final EnemyType[] enemyTypes = EnemyType.values();
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();    
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(EnemyInfo.class));

    private EntityProcessor proc = (eid) -> {
	EnemyInfo ei = (EnemyInfo) repo.getComponent(eid, EnemyInfo.class);
	SpriteMovement mv = (SpriteMovement) repo.getComponent(eid, SpriteMovement.class);
	WorldPhysics phys = (WorldPhysics) repo.getComponent(eid, WorldPhysics.class);
	if(ei == null || mv == null || phys == null) return;
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

    public EnemyUpdateAgent() {
	enemyImagesL = new EnumMap<EnemyType, Bitmap>(EnemyType.class);
	enemyImagesR = new EnumMap<EnemyType, Bitmap>(EnemyType.class);
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
