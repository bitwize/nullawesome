package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
public class ButtonRenderAgent implements RenderAgent {
    public static PointF leftButtonLoc = new PointF(16, 240);
    public static PointF rightButtonLoc = new PointF(96, 240);
    public static PointF jumpButtonLoc = new PointF(400, 240);
    public static PointF pauseButtonLoc = new PointF(432, 0);
    public static PointF backButtonLoc = new PointF(0, 0);
    public static PointF hackButtonLoc = new PointF(336, 256);
    public static float BUTTON_SIZE = 64.f;
    public static float SMALL_BUTTON_SIZE = 48.f;
    public static float HACK_TARGET_SIZE = 32.f;
    public static float HACK_TARGET_HALF_SIZE = 16.f;

    
    private Bitmap buttonBitmap;
    private Bitmap pressedButtonBitmap;
    private Bitmap smallButtonBitmap;
    private Bitmap hackTargetBitmap;
    private DrawAgent dagent;
    private Rect btnRect;
    private PointF where;
    private Canvas cvs;
    private int playerEid;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(HackTarget.class));
    private EntityProcessor proc = (eid) -> {
	drawHackTarget(cvs, eid);
    };
    private RenderAgent[] buttonRendererTable = {
	(c) -> {

	},
	(c) -> {
	    PlayerInfo pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
	    int keyStatus = pi.keyStatus;
	    drawMovementControls(c, keyStatus);
	},
	(c) -> {
	    drawHackingControls(c);
	},
	(c) -> {

	}
    };
    public ButtonRenderAgent(DrawAgent a) {
	buttonBitmap = ContentRepository.get().getBitmap("buttons");
	pressedButtonBitmap = ContentRepository.get().getBitmap("buttons_pressed");
	smallButtonBitmap = ContentRepository.get().getBitmap("smallbuttons");
	hackTargetBitmap = ContentRepository.get().getBitmap("hacktarget");
	dagent = a;
	btnRect = new Rect();
	where = new PointF();
	playerEid = EntityRepository.get().findEntityWithComponent(PlayerInfo.class);
	reh.register();
    }
    public void drawMovementControls(Canvas c, int keyStatus) {
	btnRect.left = 0;
	btnRect.top = 0;
	btnRect.right = (int)BUTTON_SIZE;
	btnRect.bottom = (int)BUTTON_SIZE;
	if((keyStatus & PlayerInfo.KEY_RIGHT) != 0) {
	    dagent.drawButton(c, pressedButtonBitmap, btnRect, rightButtonLoc);
	} else {
	    dagent.drawButton(c, buttonBitmap, btnRect, rightButtonLoc);
	}
	btnRect.offset(0, 64);
	if((keyStatus & PlayerInfo.KEY_LEFT) != 0) {
	    dagent.drawButton(c, pressedButtonBitmap, btnRect, leftButtonLoc);
	} else {
	    dagent.drawButton(c, buttonBitmap, btnRect, leftButtonLoc);
	}
	btnRect.offset(0, 64);
	if((keyStatus & PlayerInfo.KEY_JUMP) != 0) {
	    dagent.drawButton(c, pressedButtonBitmap, btnRect, jumpButtonLoc);
	} else {
	    dagent.drawButton(c, buttonBitmap, btnRect, jumpButtonLoc);
	}
	btnRect.left = 0;
	btnRect.top = (int)SMALL_BUTTON_SIZE;
	btnRect.right = (int)SMALL_BUTTON_SIZE;
	btnRect.bottom = (int)SMALL_BUTTON_SIZE * 2;
	dagent.drawButton(c, smallButtonBitmap, btnRect, hackButtonLoc);
	btnRect.offset((int)SMALL_BUTTON_SIZE, 0);
	dagent.drawButton(c, smallButtonBitmap, btnRect, pauseButtonLoc);
    }
    public void drawHackingControls(Canvas c) {
	btnRect.left = (int)SMALL_BUTTON_SIZE * 2;
	btnRect.top = (int)SMALL_BUTTON_SIZE;
	btnRect.right = (int)SMALL_BUTTON_SIZE * 3;
	btnRect.bottom = (int)SMALL_BUTTON_SIZE * 2;
	dagent.drawButton(c, smallButtonBitmap, btnRect, backButtonLoc);
	cvs = c;
	reh.processAll(proc);
    }

    public void drawHackTarget(Canvas c, int eid) {
	SpriteMovement mv = (SpriteMovement)(EntityRepository.get().getComponent(eid, SpriteMovement.class));
	HackTarget ht = (HackTarget)(EntityRepository.get().getComponent(eid, HackTarget.class));
	WorldPhysics phys = (WorldPhysics)EntityRepository.get().getComponent(eid, WorldPhysics.class);
	if(mv == null ||
	   ht == null ||
	   phys == null) return;
	SpriteMovement wmv = (SpriteMovement)(EntityRepository.get().getComponent(phys.stageEid, SpriteMovement.class));
	if(wmv == null) return;
	if(ht.hacked) return;
	float half_width = ht.width / 2.f;
	float half_height = ht.height / 2.f;
	where.set(wmv.position);
	where.negate();
	where.offset(mv.position.x, mv.position.y);
	where.offset(-half_width, -half_height);
	btnRect.left = 0;
	btnRect.top = 0;
	btnRect.right = (int)HACK_TARGET_HALF_SIZE;
	btnRect.bottom = (int)HACK_TARGET_HALF_SIZE;
	dagent.drawSprite(c, hackTargetBitmap, btnRect, where);
	where.set(wmv.position);
	where.negate();
	where.offset(mv.position.x, mv.position.y);
	where.offset(half_width - HACK_TARGET_HALF_SIZE, -half_height);
	btnRect.left = (int)HACK_TARGET_HALF_SIZE;
	btnRect.top = 0;
	btnRect.right = (int)HACK_TARGET_SIZE;
	btnRect.bottom = (int)HACK_TARGET_HALF_SIZE;
	dagent.drawSprite(c, hackTargetBitmap, btnRect, where);
	where.set(wmv.position);
	where.negate();
	where.offset(mv.position.x, mv.position.y);
	where.offset(-half_width, half_height - HACK_TARGET_HALF_SIZE);
	btnRect.left = 0;
	btnRect.top = (int)HACK_TARGET_HALF_SIZE;
	btnRect.right = (int)HACK_TARGET_HALF_SIZE;
	btnRect.bottom = (int)HACK_TARGET_SIZE;
	dagent.drawSprite(c, hackTargetBitmap, btnRect, where);
	where.set(wmv.position);
	where.negate();
	where.offset(mv.position.x, mv.position.y);
	where.offset(half_width - HACK_TARGET_HALF_SIZE, half_height - HACK_TARGET_HALF_SIZE);
	btnRect.left = (int)HACK_TARGET_HALF_SIZE;
	btnRect.top = (int)HACK_TARGET_HALF_SIZE;
	btnRect.right = (int)HACK_TARGET_SIZE;
	btnRect.bottom = (int)HACK_TARGET_SIZE;
	dagent.drawSprite(c, hackTargetBitmap, btnRect, where);
    }

    public void drawOn(Canvas c) {
	int keyStatus = 0;
	PlayerInfo pi;
	if(playerEid < 0) {
	    return;
	}
	pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
	keyStatus = pi.keyStatus;
	buttonRendererTable[pi.inputState.ordinal()].drawOn(c);
    }
}
