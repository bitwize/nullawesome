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
    private int currentStage;
    private Context myctx;
    private DrawAgent dagent;
    private GameThread thr;
    private RectF buttonHitRect;
    private ArrayList<UpdateAgent> uagents = new ArrayList<UpdateAgent>();
    private ArrayList<RenderAgent> ragents = new ArrayList<RenderAgent>();
    public PointF checkPoint = new PointF();
    public RectF checkRect = new RectF();
    private PointF where = new PointF();
    private boolean somethingHacked;
    private EntityProcessor hackTargetProcessor = (eid) -> {
        SpriteMovement mv = (SpriteMovement)(EntityRepository.get().getComponent(eid, SpriteMovement.class));
        HackTarget ht = (HackTarget)(EntityRepository.get().getComponent(eid, HackTarget.class));
        WorldPhysics phys = (WorldPhysics)EntityRepository.get().getComponent(eid, WorldPhysics.class);
        PlayerInfo pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
        if(mv == null ||
           ht == null ||
           phys == null) return;
        if(ht.hacked) return;
        if(!(ht.visible)) return;
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
            somethingHacked = true;
            ht.action.process(eid);
        }
    };

    public static interface StateEventCallback {
        public int getKeyStatus(NAView view, MotionEvent ev);
    };
    private StateEventCallback eventCallTable[] = {
        new StateEventCallback() {
            public int getKeyStatus(NAView view, MotionEvent ev) {
                return 0;
            }
        },
        new StateEventCallback() {
            public int getKeyStatus(NAView view, MotionEvent ev) {
                int a = 0;
                if(view.isPressOrRelease(ev)) {
                    a |= view.checkButtonPress(ev,
                                          ButtonRenderAgent.leftButtonLoc.x,
                                          ButtonRenderAgent.leftButtonLoc.y,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          PlayerInfo.KEY_LEFT);
                    a |= view.checkButtonPress(ev,
                                          ButtonRenderAgent.rightButtonLoc.x,
                                          ButtonRenderAgent.rightButtonLoc.y,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          PlayerInfo.KEY_RIGHT);
                    a |= view.checkButtonPress(ev,
                                          ButtonRenderAgent.jumpButtonLoc.x,
                                          ButtonRenderAgent.jumpButtonLoc.y,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          PlayerInfo.KEY_JUMP);
                    a |= view.checkButtonPress(ev,
                                          ButtonRenderAgent.hackButtonLoc.x,
                                          ButtonRenderAgent.hackButtonLoc.y,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          ButtonRenderAgent.BUTTON_SIZE,
                                          PlayerInfo.KEY_HACK);
                }
                return a;
            }
        },
        new StateEventCallback() {
            public int getKeyStatus(NAView view, MotionEvent ev) {
                int a = 0;
                if(view.isPressOrRelease(ev)) {
                    a |= view.checkButtonPress(ev,
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
                            synchronized(thr) {
                                somethingHacked = false;
                                EntityRepository.get().processEntitiesWithComponent(HackTarget.class,
                                                                                    view.hackTargetProcessor);
                                if(somethingHacked) {
                                    a |= PlayerInfo.KEY_BACK;
                                }
                            }
                        }
                    }
                }
                return a;
            }
        },
        (view, ev) -> {
            return 0;
        },
        (view, ev) -> {
            return 0;
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
    private void initGameState() {
        try {
            initStage();
            initPlayer();
            initFinalDoor();
            uagents.add(new PositionUpdateAgent());
            uagents.add(new PhysicsUpdateAgent());
            uagents.add(new CollisionUpdateAgent());
            uagents.add(new ElevatorUpdateAgent());
            uagents.add(new DoorUpdateAgent());
            uagents.add(new EnemyUpdateAgent());
            uagents.add(new CollectibleUpdateAgent());
            uagents.add(new PlayerUpdateAgent(playerEid));
            uagents.add(new CameraUpdateAgent());
            uagents.add(new TimerUpdateAgent());
            uagents.add(new GameResetAgent(this));
            ragents.add(new BackgroundRenderAgent(dagent, stageEid));
            ragents.add(new SceneryDisplayAgent(dagent, stageEid));
            ragents.add(new SpriteDisplayAgent(dagent));
            ragents.add(new TextRenderAgent(dagent));
            ragents.add(new ButtonRenderAgent(dagent));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void init() {
        setFocusableInTouchMode(true);
        requestFocus();
        buttonHitRect = new RectF();
        dagent = new DrawAgent(ragents);
        dagent.setHolder(this.getHolder());
        this.getHolder().addCallback(this);
        initGameState();
    }

    public void reset() {
        synchronized(thr) {
            uagents.clear();
            ragents.clear();
            EntityRepository.get().clear();
            initGameState();
        }
    }

    private void initStage() throws InvalidEntityException {
        try { stageEid = EntityRepository.get().newEntity(); }
        catch(EntityTableFullException e) { return; }
        StageInfo info = StageInfo.getInfoNamed(ContentRepository.get().getStageNameAt(currentStage));
        SpriteMovement mv = new SpriteMovement();
        mv.position = new PointF();
        mv.velocity = new PointF();
        mv.acceleration = new PointF();
        EntityRepository.get().addComponent(stageEid, info);
        EntityRepository.get().addComponent(stageEid, mv);
        try {
            ThingFactory.createThings(stageEid);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initPlayer() throws InvalidEntityException {
        try { playerEid = EntityRepository.get().newEntity(); }
        catch(EntityTableFullException e) { return; }
        StageInfo sti =
            (StageInfo)EntityRepository.get().getComponent(stageEid,
                                                           StageInfo.class);
        WorldPhysics phys = new WorldPhysics();
        SpriteShape shp = new SpriteShape();
        PlayerInfo pli = new PlayerInfo();
        shp.shapes = ContentRepository.get().getBitmap("player_r");
        shp.subsection = new Rect(0, 0, 32, 32);
        SpriteMovement mv = new SpriteMovement();
        mv.position.set(sti.playerStartX, sti.playerStartY);
        mv.hotspot.set(16, 16);
        phys.stageEid = stageEid;
        phys.state = WorldPhysics.State.GROUNDED;
        phys.gvelmax = 2.f;
        phys.radius = 16;
        phys.hitbox.left = -10.f;
        phys.hitbox.top = -16.f;
        phys.hitbox.right = 10.f;
        phys.hitbox.bottom = 16.f;
        phys.collider = new ElevatorCollider();
        phys.collisionCriterion = (eid) -> EntityRepository.get()
            .getComponent(eid, ElevatorStates.class) != null;
        EntityRepository.get().addComponent(playerEid, shp);
        EntityRepository.get().addComponent(playerEid, mv);
        EntityRepository.get().addComponent(playerEid, phys);
        EntityRepository.get().addComponent(playerEid, pli);
        EntityRepository.get().processEntitiesWithComponent(EnemyInfo.class, (anEid) -> {
                EnemyInfo ei = (EnemyInfo)EntityRepository.get().getComponent(anEid, EnemyInfo.class);
                ei.targetEid = playerEid;
        });
    }

    private void initFinalDoor() throws InvalidEntityException {
        int doorEid;
        try { doorEid = EntityRepository.get().newEntity(); }
        catch(EntityTableFullException e) { return; }
        StageInfo sti =
            (StageInfo)EntityRepository.get().getComponent(stageEid,
                                                           StageInfo.class);
        sti.map.setTileWorldCoords(sti.goalX, sti.goalY, (short) (FinalDoorInfo.GOAL_TILE_START + 3));
        sti.map.setTileWorldCoords(sti.goalX - TileMap.TILE_SIZE,
                                   sti.goalY,
                        (short) (FinalDoorInfo.GOAL_TILE_START + 2));
        sti.map.setTileWorldCoords(sti.goalX,
                                   sti.goalY - TileMap.TILE_SIZE,
                        (short) (FinalDoorInfo.GOAL_TILE_START + 1));
        sti.map.setTileWorldCoords(sti.goalX - TileMap.TILE_SIZE,
                                   sti.goalY - TileMap.TILE_SIZE,
                                   FinalDoorInfo.GOAL_TILE_START);
        WorldPhysics phys = new WorldPhysics();
        SpriteShape shp = SpriteShape.loadAnimation(ContentRepository.get().getAnimation("end_door_anim"));
        FinalDoorInfo fdi = new FinalDoorInfo();
        SpriteMovement mv = new SpriteMovement();
        mv.position.set(sti.goalX, sti.goalY);
        mv.hotspot.set(FinalDoorInfo.FD_HOTSPOT_X, FinalDoorInfo.FD_HOTSPOT_Y);
        mv.zOrder = 0;
        phys.stageEid = stageEid;
        phys.state = WorldPhysics.State.FALLING;
        phys.gvelmax = 2.f;
        phys.gravity.y = 0.f;
        phys.radius = 16;
        EntityRepository.get().addComponent(doorEid, shp);
        EntityRepository.get().addComponent(doorEid, mv);
        EntityRepository.get().addComponent(doorEid, phys);
        EntityRepository.get().addComponent(doorEid, fdi);
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        thr = new GameThread(dagent,uagents);
        thr.startRunning();
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(thr != null) {
            thr.stopRunning();
        }
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
            if(((ev.getActionMasked() != MotionEvent.ACTION_POINTER_UP) ||
                (ev.getActionIndex() != i)) &&
               buttonHitRect.contains((float)ev.getX(i), (float)ev.getY(i))) {
                return buttonBit;
            }
        }
        return 0;
    }

    private static boolean isPressOrRelease(MotionEvent ev) {
        int action = ev.getActionMasked();
        
        return action == MotionEvent.ACTION_DOWN ||
            action == MotionEvent.ACTION_POINTER_DOWN ||
            action == MotionEvent.ACTION_MOVE ||
            action == MotionEvent.ACTION_POINTER_UP;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        synchronized(thr) {
            PlayerInfo pi;
            pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
            if(pi == null) return true;
            pi.keyStatus = eventCallTable[pi.inputState.ordinal()].getKeyStatus(this, ev);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent ke) {
        synchronized(thr) {
            PlayerInfo pi;
            pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
            if(pi == null) return true;
            switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                pi.keyStatus |= PlayerInfo.KEY_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                pi.keyStatus |= PlayerInfo.KEY_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_SPACE:
                pi.keyStatus |= PlayerInfo.KEY_JUMP;
                break;
            case KeyEvent.KEYCODE_Z:
                pi.keyStatus |= PlayerInfo.KEY_HACK;
                break;
            }
        }
        return super.onKeyDown(keyCode, ke);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent ke) {
        synchronized(thr) {
            PlayerInfo pi;
            pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
            if(pi == null) return true;
            switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                pi.keyStatus &= ~PlayerInfo.KEY_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                pi.keyStatus &= ~PlayerInfo.KEY_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_SPACE:
                pi.keyStatus &= ~PlayerInfo.KEY_JUMP;
                break;
            case KeyEvent.KEYCODE_Z:
                pi.keyStatus &= ~PlayerInfo.KEY_HACK;
                break;
            }
        }
        return super.onKeyUp(keyCode, ke);
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
