package bitwize.nullawesome;

import java.util.ArrayList;
import android.graphics.*;

public class EndScreenRenderAgent implements RenderAgent {
    private DrawAgent dagent;
    private Bitmap texture;
    private EntityRepository repo;
    private ArrayList<Short> digitsList = new ArrayList<Short>();
    private PointF digitsPoint = new PointF();
    private static final Rect collectiblesRect = new Rect(0, 0, 147, 26);
    private static final Rect intelRect = new Rect(0, 26, 59, 52);
    private static final Rect scoreRect = new Rect(0, 56, 79, 75);
    private static final Rect digit0Rect = new Rect(0, 82, 30, 110);
    private static final Rect digit1Rect = new Rect(40, 82, 51, 110);
    private static final Rect digit2Rect = new Rect(58, 82, 88, 110);
    private static final Rect digit3Rect = new Rect(97, 82, 128, 110);
    private static final Rect digit4Rect = new Rect(136, 82, 166, 110);
    private static final Rect digit5Rect = new Rect(0, 137, 30, 165);
    private static final Rect digit6Rect = new Rect(39, 137, 69, 165);
    private static final Rect digit7Rect = new Rect(78, 137, 109, 165);
    private static final Rect digit8Rect = new Rect(117, 137, 147, 165);
    private static final Rect digit9Rect = new Rect(156, 137, 187, 165);
    private static final Rect[] digitRects = {
        digit0Rect,
        digit1Rect,
        digit2Rect,
        digit3Rect,
        digit4Rect,
        digit5Rect,
        digit6Rect,
        digit7Rect,
        digit8Rect,
        digit9Rect
    };
    private static final Rect slashRect = new Rect(196, 137, 226, 165);
    private static final Rect starRect = new Rect(98, 28, 150, 75);

    private static final PointF collectiblesLabelLoc = new PointF(48, 60);
    private static final PointF collectiblesCollectedLoc = new PointF(256, 58);
    private static final PointF collectiblesSlashLoc = new PointF(350, 58);
    private static final PointF collectiblesTotalLoc = new PointF(380, 58);
    private static final PointF intelLabelLoc = new PointF(48, 120);
    private static final PointF intelCollectedLoc = new PointF(320, 118);
    private static final PointF intelSlashLoc = new PointF(350, 118);
    private static final PointF intelTotalLoc = new PointF(380, 118);
    private static final PointF scoreLabelLoc = new PointF(48, 180);
    private static final PointF scoreStarsLoc = new PointF(256, 162);
    public EndScreenRenderAgent(DrawAgent a) {
        repo = EntityRepository.get();
        dagent = a;
        texture = ContentRepository.get().getBitmap("endscreen_text");        
    }

    private void drawDigits(Canvas c, short n, PointF loc) {
        digitsList.clear();
        short n2 = n;
        short totalWidth = 0;
        do {
            digitsList.add((short)(n2 % 10));
            n2 /= 10;
        } while(n2 != 0);
        digitsPoint.set(loc);
        for(int i=digitsList.size() - 1; i>=0; i--) {
            Rect r = digitRects[digitsList.get(i)];
            dagent.drawSprite(c, texture, r, digitsPoint);
            digitsPoint.x += r.right - r.left;
        }
    }
    
    public void drawOn(Canvas c) {
        int playerEid = repo.findEntityWithComponent(PlayerInfo.class);
        if(playerEid == EntityRepository.NO_ENTITY) {
            return;
        }
        PlayerInfo playerinfo = (PlayerInfo)repo.getComponent(playerEid, PlayerInfo.class);
        if(playerinfo == null) {
            return;
        }
        if(playerinfo.inputState != InputState.END_STAGE) {
            return;
        }
        EndScreenInfo endscreeninfo = (EndScreenInfo)repo.getComponent(playerEid, EndScreenInfo.class);
        if(endscreeninfo == null) {
            return;
        }
        dagent.drawSprite(c, texture, collectiblesRect, collectiblesLabelLoc);
        dagent.drawSprite(c, texture, intelRect, intelLabelLoc);
        dagent.drawSprite(c, texture, scoreRect, scoreLabelLoc);
        drawDigits(c, endscreeninfo.collectiblesCollected, collectiblesCollectedLoc);
        dagent.drawSprite(c, texture, slashRect, collectiblesSlashLoc);
        drawDigits(c, endscreeninfo.collectiblesTotal, collectiblesTotalLoc);
        drawDigits(c, endscreeninfo.intelCollected, intelCollectedLoc);
        dagent.drawSprite(c, texture, slashRect, intelSlashLoc);
        drawDigits(c, endscreeninfo.intelTotal, intelTotalLoc);
        digitsPoint.set(scoreStarsLoc);
        for(int i=0; i<endscreeninfo.score; i++) {
            dagent.drawSprite(c, texture, starRect, digitsPoint);
            digitsPoint.x += (starRect.right - starRect.left);
        }
    }
}
