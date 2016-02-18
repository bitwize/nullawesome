package bitwize.nullawesome;

public class PositionUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc;
    public PositionUpdateAgent() {
	repo = EntityRepository.get();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    SpriteMovement mov;
		    try {
			mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		    }
		    catch(InvalidEntityException e) { return; }
		    mov.position.offset(mov.velocity.x, mov.velocity.y);
		    mov.velocity.offset(mov.acceleration.x, mov.acceleration.y);
		}
	    };

    }
    public void update(long time) {
	repo.processEntitiesWithComponent(SpriteMovement.class, proc);
    }

}
