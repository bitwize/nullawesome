package bitwize.nullawesome;

import android.graphics.*;
public class EndScreenRenderAgent implements RenderAgent {
    private DrawAgent dagent;
    private Bitmap texture;
    private EntityRepository repo;
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
    private static final Rect slashRect = new Rect(196, 137, 226, 165);
    private static final Rect starRect = new Rect(98, 28, 150, 75);

    private static final PointF collectiblesLabelLoc = new PointF(48, 60);
    private static final PointF intelLabelLoc = new PointF(48, 120);
    private static final PointF scoreLabelLoc = new PointF(48, 180);
    public EndScreenRenderAgent(DrawAgent a) {
        repo = EntityRepository.get();
        dagent = a;
        texture = ContentRepository.get().getBitmap("endscreen_text");        
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
        if(playerinfo.inputState != InputState.EXIT_LEVEL) {
            return;
        }
        dagent.drawSprite(c, texture, collectiblesRect, collectiblesLabelLoc);
        dagent.drawSprite(c, texture, intelRect, intelLabelLoc);
        dagent.drawSprite(c, texture, scoreRect, scoreLabelLoc);
    }
}
