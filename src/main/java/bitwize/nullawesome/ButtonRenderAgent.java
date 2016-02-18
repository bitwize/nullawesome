package bitwize.nullawesome;

import android.graphics.*;
import android.util.Log;
public class ButtonRenderAgent implements RenderAgent {
    public static PointF leftButtonLoc = new PointF(16, 240);
    public static PointF rightButtonLoc = new PointF(96, 240);
    public static PointF jumpButtonLoc = new PointF(400, 240);
    public static float BUTTON_SIZE = 64.f;

    private Bitmap buttonBitmap;
    private Bitmap pressedButtonBitmap;
    private DrawAgent dagent;
    private Rect btnRect;
    public ButtonRenderAgent(DrawAgent a) {
	buttonBitmap = ContentRepository.get().getBitmap("buttons");
	pressedButtonBitmap = ContentRepository.get().getBitmap("buttons_pressed");
	dagent = a;
	btnRect = new Rect();
    }
    public void drawOn(Canvas c) {
	int keyStatus = 0;
	int playerEid = EntityRepository.get().findEntityWithComponent(PlayerInfo.class);
	if(playerEid >= 0) {
	    try { keyStatus = ((PlayerInfo)EntityRepository.get().getComponent(playerEid, PlayerInfo.class)).keyStatus; }
	    catch(InvalidEntityException e) {}
	}

	btnRect.left = 0;
	btnRect.top = 0;
	btnRect.right = (int)BUTTON_SIZE;
	btnRect.bottom = (int)BUTTON_SIZE;
	if((keyStatus & PlayerInfo.KEY_RIGHT) != 0) {
	    dagent.drawSprite(c, pressedButtonBitmap, btnRect, rightButtonLoc);
	} else {
	    dagent.drawSprite(c, buttonBitmap, btnRect, rightButtonLoc);
	}
	btnRect.offset(0, 64);
	if((keyStatus & PlayerInfo.KEY_LEFT) != 0) {
	    dagent.drawSprite(c, pressedButtonBitmap, btnRect, leftButtonLoc);
	} else {
	    dagent.drawSprite(c, buttonBitmap, btnRect, leftButtonLoc);
	}
	btnRect.offset(0, 64);
	if((keyStatus & PlayerInfo.KEY_JUMP) != 0) {
	    dagent.drawSprite(c, pressedButtonBitmap, btnRect, jumpButtonLoc);
	} else {
	    dagent.drawSprite(c, buttonBitmap, btnRect, jumpButtonLoc);
	}
    }
}
