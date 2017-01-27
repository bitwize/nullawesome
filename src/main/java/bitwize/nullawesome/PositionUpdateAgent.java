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
		    if(mov == null) return;
		    mov.position.offset(mov.velocity.x, mov.velocity.y);
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
				shp.currentFrame = 0;
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
