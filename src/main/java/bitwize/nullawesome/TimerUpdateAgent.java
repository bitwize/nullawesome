package bitwize.nullawesome;

public class TimerUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(TimerAction.class));
    private EntityProcessor proc = (eid) -> {
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
    };
    public TimerUpdateAgent() {
	repo = EntityRepository.get();
	reh.register();
    }
    public void update(long time) {
	reh.processAll(proc);
    }
}
