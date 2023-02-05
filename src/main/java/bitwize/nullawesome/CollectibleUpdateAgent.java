package bitwize.nullawesome;

import java.util.ArrayList;

public class CollectibleUpdateAgent implements UpdateAgent {

    public static final int[] bobbingDisplacements = new int[] { 0, -1, -2, -1, 0, 1, 2, 1 };
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(CollectibleInfo.class));
    private EntityRepository repo = EntityRepository.get();
    private static int delay = 0;
    private static final int MAX_DELAY = 3;
    private EntityProcessor proc = (eid) -> {
	SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
	SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	CollectibleInfo ci = (CollectibleInfo)repo.getComponent(eid, CollectibleInfo.class);
	switch(ci.state) {
	case STATIONARY:
	    if(delay == 0) {
		mv.position.y += bobbingDisplacements[ci.bobbingFrame];
		ci.bobbingFrame++;
		ci.bobbingFrame &= 7;
	    }
	    break;
	case VANISHING:
	    if((shp.currentFrame == shp.frames.length - 1)
	       && (shp.currentTime == shp.timings[shp.currentFrame] - 1)) {
		ci.state = CollectibleState.COLLECTED;
	    }
	    break;
	case COLLECTED:       
	    if((shp.maxFrames > 0)) {
		shp.subsection.set(0, 0, 0, 0);
		shp.currentFrame = 0;
		shp.maxFrames = 0;
	    }
	    break;
	}
    };
    public CollectibleUpdateAgent() {
	reh.register();
    }
    public void update(long time) {
	reh.processAll(proc);
	delay++;
	if(delay >= MAX_DELAY) {
	    delay = 0;
	}
    }
};
