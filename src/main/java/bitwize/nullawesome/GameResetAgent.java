package bitwize.nullawesome;

public class GameResetAgent implements UpdateAgent {
    private NAView myView;
        private int timer = 0;

        private int POSTDEATH_DELAY=350;

    public GameResetAgent(NAView v) {
        myView = v;
    }


    public void update(long time) {
        EntityRepository repo = EntityRepository.get();
        int playerEid = repo.findEntityWithComponent(PlayerInfo.class);
        if(playerEid == EntityRepository.NO_ENTITY) return;
        PlayerInfo info = (PlayerInfo)repo.getComponent(playerEid, PlayerInfo.class);
        SpriteMovement mov = (SpriteMovement)repo.getComponent(playerEid, SpriteMovement.class);
        if(info == null || mov == null) return;
        if(info.inputState == InputState.DEATH) {
            timer++;
            if(timer > POSTDEATH_DELAY) {
                myView.reset();
            }
        } else if((info.inputState == InputState.MOVEMENT) &&
                  ((info.keyStatus & PlayerInfo.KEY_PAUSE) != 0)) {
            info.inputState = InputState.PAUSE;
            info.keyStatus &= ~PlayerInfo.KEY_PAUSE;
            myView.pauseGame();
        } else if((info.inputState == InputState.PAUSE) &&
                  ((info.keyStatus & PlayerInfo.KEY_PAUSE) != 0)) {
            info.inputState = InputState.MOVEMENT;
            info.keyStatus &= ~PlayerInfo.KEY_PAUSE;
            myView.resumeGame();
        }
    }
}
