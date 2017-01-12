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
	    dagent = new DrawAgent(ragents);
	    dagent.setHolder(this.getHolder());
	    uagents.add(new PositionUpdateAgent());
	    uagents.add(new PhysicsUpdateAgent());
	    uagents.add(new PlayerUpdateAgent());
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
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	int a = 0;
	PlayerInfo pi;
	pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
	if(pi == null) return true;
	if((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN ||
	   (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN ||
	   (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE ||
	   (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
	    buttonHitRect.left = ButtonRenderAgent.leftButtonLoc.x;
	    buttonHitRect.top = ButtonRenderAgent.leftButtonLoc.y;
	    buttonHitRect.right = buttonHitRect.left + ButtonRenderAgent.BUTTON_SIZE;
	    buttonHitRect.bottom = buttonHitRect.top + ButtonRenderAgent.BUTTON_SIZE;
	    scaleRectToScreen(buttonHitRect);
	    for(int i=0;i<ev.getPointerCount();i++) {
		if((((ev.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_POINTER_UP) ||
		    (((ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT)
		     != i)) &&
		   buttonHitRect.contains((float)ev.getX(i), (float)ev.getY(i))) {
		    a |= PlayerInfo.KEY_LEFT;
		}
	    }
	    buttonHitRect.left = ButtonRenderAgent.rightButtonLoc.x;
	    buttonHitRect.top = ButtonRenderAgent.rightButtonLoc.y;
	    buttonHitRect.right = buttonHitRect.left + ButtonRenderAgent.BUTTON_SIZE;
	    buttonHitRect.bottom = buttonHitRect.top + ButtonRenderAgent.BUTTON_SIZE;
	    scaleRectToScreen(buttonHitRect);
	    for(int i=0;i<ev.getPointerCount();i++) {
		if((((ev.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_POINTER_UP) ||
		    (((ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT)
		     != i)) &&
		   buttonHitRect.contains((float)ev.getX(i), (float)ev.getY(i))) {
		    a |= PlayerInfo.KEY_RIGHT;
		}
	    }
	    buttonHitRect.left = ButtonRenderAgent.jumpButtonLoc.x;
	    buttonHitRect.top = ButtonRenderAgent.jumpButtonLoc.y;
	    buttonHitRect.right = buttonHitRect.left + ButtonRenderAgent.BUTTON_SIZE;
	    buttonHitRect.bottom = buttonHitRect.top + ButtonRenderAgent.BUTTON_SIZE;
	    scaleRectToScreen(buttonHitRect);
	    for(int i=0;i<ev.getPointerCount();i++) {
		if((((ev.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_POINTER_UP) ||
		    (((ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT)
		     != i)) &&
		   buttonHitRect.contains((float)ev.getX(i), (float)ev.getY(i))) {
		    a |= PlayerInfo.KEY_JUMP;
		}
	    }
	}
	pi.keyStatus = a;
	return true;
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
