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

    private Bitmap buttonBitmap;
    private Bitmap pressedButtonBitmap;
    private Bitmap smallButtonBitmap;
    private DrawAgent dagent;
    private Rect btnRect;
    public ButtonRenderAgent(DrawAgent a) {
	buttonBitmap = ContentRepository.get().getBitmap("buttons");
	pressedButtonBitmap = ContentRepository.get().getBitmap("buttons_pressed");
	smallButtonBitmap = ContentRepository.get().getBitmap("smallbuttons");
	dagent = a;
	btnRect = new Rect();
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
	btnRect.top = 0;
	btnRect.right = (int)SMALL_BUTTON_SIZE;
	btnRect.bottom = (int)SMALL_BUTTON_SIZE;
	dagent.drawButton(c, smallButtonBitmap, btnRect, hackButtonLoc);
	btnRect.offset((int)SMALL_BUTTON_SIZE, 0);
	dagent.drawButton(c, smallButtonBitmap, btnRect, pauseButtonLoc);
    }
    public void drawHackingControls(Canvas c) {
    }
    public void drawOn(Canvas c) {
	int keyStatus = 0;
	PlayerInfo pi;
	int playerEid = EntityRepository.get().findEntityWithComponent(PlayerInfo.class);
	if(playerEid < 0) {
	    return;
	}
	pi = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class));
	keyStatus = pi.keyStatus;
	switch(pi.inputState) {
	case MOVEMENT:
	    drawMovementControls(c, keyStatus);
	case HACKING:
	    drawHackingControls(c);
	}
    }
}
