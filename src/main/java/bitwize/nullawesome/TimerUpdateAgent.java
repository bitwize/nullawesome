package bitwize.nullawesome;

public class TimerUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc = new EntityProcessor() {
	    public void process(int eid) {
		TimerAction ta = (TimerAction)repo.getComponent(eid, TimerAction.class);
		if(ta == null) return;
		if(ta.active) {
		    ta.nTicks--;
		    if(ta.nTicks == 0) {
			ta.active = false;
			ta.nTicks = ta.maxTicks;
			ta.action.process(eid);
		    }
		}
	    }
	};
    public TimerUpdateAgent() {
	repo = EntityRepository.get();
    }
    public void update(long time) {
	repo.processEntitiesWithComponent(TimerAction.class, proc);	
    }
}
