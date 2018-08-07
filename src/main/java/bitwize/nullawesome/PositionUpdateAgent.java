package bitwize.nullawesome;

public class PositionUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc;
    public PositionUpdateAgent() {
	repo = EntityRepository.get();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    SpriteMovement mov;
		    SpriteShape shp;
		    mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		    WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
		    if(mov == null) return;
		    mov.position.offset(mov.velocity.x, mov.velocity.y);
		    if(phys != null && phys.currentElevatorEid != EntityRepository.NO_ENTITY) {
			SpriteMovement movE = (SpriteMovement)repo.getComponent(phys.currentElevatorEid, SpriteMovement.class);
			if(movE != null) {
			    mov.position.x +=
				movE.position.x - phys.currentElevatorPosition.x;
			    mov.position.y +=
				movE.position.y - phys.currentElevatorPosition.y;
			    phys.currentElevatorPosition.set(movE.position);
			}
		    }
		    mov.velocity.offset(mov.acceleration.x, mov.acceleration.y);
		    shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
		    if(shp == null) return;
		    if(shp.maxFrames > 0) {
			int frameHeight = shp.shapes.getHeight() / shp.maxFrames;
			shp.currentTime++;
			if(shp.currentTime >= shp.timings[shp.currentFrame]) {
			    shp.currentTime = 0;
			    shp.currentFrame++;
			    if(shp.currentFrame >= shp.frames.length) {
				    shp.currentFrame = shp.loop
					    ? 0
					    : shp.frames.length - 1;
			    }
			}
			
			// Update the sprite's current shape with the
			// rectangle corresponding to the current
			// frame.

			shp.subsection.set(0, shp.frames[shp.currentFrame] * frameHeight, 
					   shp.shapes.getWidth(), (shp.frames[shp.currentFrame] + 1) * frameHeight);
		    }
		}
	    };

    }
    public void update(long time) {
	repo.processEntitiesWithComponent(SpriteMovement.class, proc);
    }

}
