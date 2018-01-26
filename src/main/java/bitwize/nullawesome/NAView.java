package bitwize.nullawesome;

import java.util.ArrayList;
import android.view.*;
import android.graphics.*;
import android.content.Context;
import android.util.AttributeSet;


public class NAView extends SurfaceView implements SurfaceHolder.Callback
{

    private int stageEid;
    private int playerEid;
    private Context myctx;
    private DrawAgent dagent;
    private GameThread thr;
    private RectF buttonHitRect;
    private ArrayList<UpdateAgent> uagents = new ArrayList<UpdateAgent>();
    private ArrayList<RenderAgent> ragents = new ArrayList<RenderAgent>();
    public PointF checkPoint = new PointF();
    public RectF checkRect = new RectF();
    private PointF where = new PointF();
    private EntityProcessor hackTargetProcessor = new EntityProcessor() {
	    public void process(int eid) {
		SpriteMovement mv = (SpriteMovement)(EntityRepository.get().getComponent(eid, SpriteMovement.class));
		HackTarget ht = (HackTarget)(EntityRepository.get().getComponent(eid, HackTarget.class));
		WorldPhysics phys = (WorldPhysics)EntityRepository.get().getComponent(eid, WorldPhysics.class);
		if(mv == null ||
		   ht == null ||
		   phys == null) return;
		if(ht.hacked) return;
		SpriteMovement wmv = (SpriteMovement)(EntityRepository.get().getComponent(phys.stageEid, SpriteMovement.class));
		if(wmv == null) return;
		float half_width = ht.width / 2.f;
		float half_height = ht.height / 2.f;
		where.set(wmv.position);
		where.negate();
		where.offset(mv.position.x, mv.position.y);
		checkRect.set(where.x - half_width, where.y - half_height,
			      where.x + half_width, where.y + half_height);
		scaleRectToScreen(checkRect);
		if(checkRect.contains(checkPoint.x, checkPoint.y)) {
		    ht.hacked = true;
		}
	    }
	};
    public NAView(Context ctx) {
	super(ctx);
	myctx = ctx;
	init();
    }
    public NAView(Context ctx, AttributeSet attrs) {
	super(ctx, attrs);
	myctx = ctx;
	init();
    }
    private void init() {
	try {
	    buttonHitRect = new RectF();
	    this.getHolder().addCallback(this);
	    initStage();
	    initPlayer();
	    initTestTarget();
	    dagent = new DrawAgent(ragents);
	    dagent.setHolder(this.getHolder());
	    uagents.add(new PositionUpdateAgent());
	    uagents.add(new PhysicsUpdateAgent());
	    uagents.add(new PlayerUpdateAgent(playerEid));
	    uagents.add(new CameraUpdateAgent());
	    ragents.add(new SceneryDisplayAgent(dagent, stageEid));
	    ragents.add(new SpriteDisplayAgent(dagent));
	    ragents.add(new ButtonRenderAgent(dagent));
	    thr = new GameThread(dagent,uagents);
	}
	catch(Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private void initStage() throws InvalidEntityException {
	try { stageEid = EntityRepository.get().newEntity(); }
	catch(EntityTableFullException e) { return; }
	StageInfo info = StageInfo.getTestInfo();
	SpriteMovement mv = new SpriteMovement();
	mv.position = new PointF();
	mv.velocity = new PointF();
	mv.acceleration = new PointF();
	EntityRepository.get().addComponent(stageEid, info);
	EntityRepository.get().addComponent(stageEid, mv);
    }

    private void initPlayer() throws InvalidEntityException {
	try { playerEid = EntityRepository.get().newEntity(); }
	catch(EntityTableFullException e) { return; }
	WorldPhysics phys = new WorldPhysics();
	SpriteShape shp = new SpriteShape();
	PlayerInfo pli = new PlayerInfo();
	shp.shapes = ContentRepository.get().getBitmap("player_r");
	shp.subsection = new Rect(0, 0, 32, 32);
	SpriteMovement mv = new SpriteMovement();
	mv.position.set(150, 240);
	mv.hotspot.set(16, 16);
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.GROUNDED;
	phys.gvelmax = 2.f;
	phys.radius = 16;
	phys.hitbox.left = -10;
	phys.hitbox.top = -16;
	phys.hitbox.right = 10;
	phys.hitbox.bottom = 16;
	EntityRepository.get().addComponent(playerEid, shp);
	EntityRepository.get().addComponent(playerEid, mv);
	EntityRepository.get().addComponent(playerEid, phys);
	EntityRepository.get().addComponent(playerEid, pli);
    }
    private void initTestTarget() throws InvalidEntityException {
	int targetEid;
	try { targetEid = EntityRepository.get().newEntity(); }
	catch(EntityTableFullException e) { return; }
	WorldPhysics phys = new WorldPhysics();
	SpriteMovement mv = new SpriteMovement();
	HackTarget ht = new HackTarget();
	ht.width = 64;
	ht.height = 64;
	mv.position.set(150, 240);
	mv.hotspot.set(16, 16);
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.GROUNDED;
	phys.gvelmax = 2.f;
	phys.radius = 16;
	phys.hitbox.left = -10;
	phys.hitbox.top = -16;
	phys.hitbox.right = 10;
	phys.hitbox.bottom = 16;
	EntityRepository.get().addComponent(targetEid, mv);
	EntityRepository.get().addComponent(targetEid, phys);
	EntityRepository.get().addComponent(targetEid, ht);

    }
    public void surfaceCreated(SurfaceHolder holder) {
	thr = new GameThread(dagent,uagents);
	thr.startRunning();
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
	thr.stopRunning();
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	synchronized(thr) { dagent.draw(); }
    }

    private int checkButtonPress(MotionEvent ev, float left, float top, float xSize, float ySize, int buttonBit) {
	buttonHitRect.left = left;
	buttonHitRect.top = top;
	buttonHitRect.right = left + xSize;
	buttonHitRect.bottom = top + ySize;
	scaleRectToScreen(buttonHitRect);
	for(int i=0;i<ev.getPointerCount();i++) {
	    if((((ev.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_POINTER_UP) ||
		(((ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT)
		 != i)) &&
	       buttonHitRect.contains((float)ev.getX(i), (float)ev.getY(i))) {
		return buttonBit;
	    }
	}
	return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	int a = 0;
	PlayerInfo pi;
	pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
	if(pi == null) return true;

	switch(pi.inputState) {
	case MOVEMENT:
	    if((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN ||
	       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN ||
	       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE ||
	       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
		a |= checkButtonPress(ev,
				      ButtonRenderAgent.leftButtonLoc.x,
				      ButtonRenderAgent.leftButtonLoc.y,
				      ButtonRenderAgent.BUTTON_SIZE,
				      ButtonRenderAgent.BUTTON_SIZE,
				      PlayerInfo.KEY_LEFT);
		a |= checkButtonPress(ev,
				      ButtonRenderAgent.rightButtonLoc.x,
				      ButtonRenderAgent.rightButtonLoc.y,
				      ButtonRenderAgent.BUTTON_SIZE,
				      ButtonRenderAgent.BUTTON_SIZE,
				      PlayerInfo.KEY_RIGHT);
		a |= checkButtonPress(ev,
				      ButtonRenderAgent.jumpButtonLoc.x,
				      ButtonRenderAgent.jumpButtonLoc.y,
				      ButtonRenderAgent.BUTTON_SIZE,
				      ButtonRenderAgent.BUTTON_SIZE,
				      PlayerInfo.KEY_JUMP);
		a |= checkButtonPress(ev,
				      ButtonRenderAgent.hackButtonLoc.x,
				      ButtonRenderAgent.hackButtonLoc.y,
				      ButtonRenderAgent.BUTTON_SIZE,
				      ButtonRenderAgent.BUTTON_SIZE,
				      PlayerInfo.KEY_HACK);
	    }
	    pi.keyStatus = a;
	    return true;
	case HACKING:
	    if((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN ||
	       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN ||
	       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE ||
	       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
		a |= checkButtonPress(ev,
				      ButtonRenderAgent.backButtonLoc.x,
				      ButtonRenderAgent.backButtonLoc.y,
				      ButtonRenderAgent.BUTTON_SIZE,
				      ButtonRenderAgent.BUTTON_SIZE,
				      PlayerInfo.KEY_BACK);
		for(int i=0;i<ev.getPointerCount();i++) {
		    if((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN ||
		       (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
			checkPoint.x = ev.getX(i);
			checkPoint.y = ev.getY(i);
			EntityRepository.get().processEntitiesWithComponent(HackTarget.class, hackTargetProcessor);
		    }
		}
	    }
	    pi.keyStatus = a;
	    return true;
	default:
	    return true;
	}
    }
    
    private void scaleRectToScreen(RectF r) {
	float wRat = (float)getWidth() / (float)DrawAgent.HRES;
	float hRat = (float)getHeight() / (float)DrawAgent.VRES;
	r.left *= wRat;
	r.top *= hRat;
	r.right *= wRat;
	r.bottom *= hRat;
    }

}
