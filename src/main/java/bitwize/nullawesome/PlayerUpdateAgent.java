package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class PlayerUpdateAgent implements UpdateAgent {
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();
    private Bitmap rightBitmap;
    private Bitmap leftBitmap;
    private SpriteShape standShape;
    private SpriteShape walkShape;
    private SpriteShape jumpShape;
    private SpriteShape hackShape;
    private SpriteShape putAwayShape;
    private SpriteShape dieShape;
    private int player_eid;
    private PointF pt1 = new PointF();
    private RelevantEntitiesHolder htReh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(HackTarget.class));
    private EntityProcessor htProc = (eid) -> {
        HackTarget ht = (HackTarget)EntityRepository.get().getComponent(eid, HackTarget.class);
        SpriteMovement mv = (SpriteMovement)EntityRepository.get().getComponent(eid, SpriteMovement.class);
        SpriteMovement pmv = (SpriteMovement)EntityRepository.get().getComponent(player_eid, SpriteMovement.class);
        WorldPhysics pphys = (WorldPhysics)EntityRepository.get().getComponent(player_eid, WorldPhysics.class);
        if(ht == null || mv == null || pmv == null) return;
        StageInfo info = (StageInfo)repo.getComponent(pphys.stageEid, StageInfo.class);
        if(info == null) return;
        TileMap map = info.map;
        if(EnemyUpdateAgent.hasLineOfSight(pmv.position, mv.position, map)) {
            ht.visible = true;
        } else {
            ht.visible = false;
        }
    };
    public static final float WALK_ACCEL = 0.2f;
    public PlayerUpdateAgent(int eid) {
        player_eid = eid;
        rightBitmap = content.getBitmap("player_r");
        leftBitmap = content.getBitmap("player_l");
        standShape = SpriteShape.loadAnimation(content.getAnimation("player_stand"));
        walkShape = SpriteShape.loadAnimation(content.getAnimation("player_walk"));
        jumpShape = SpriteShape.loadAnimation(content.getAnimation("player_jump"));
        hackShape = SpriteShape.loadAnimation(content.getAnimation("player_hack"));
        putAwayShape = SpriteShape.loadAnimation(content.getAnimation("player_putaway"));
        dieShape = SpriteShape.loadAnimation(content.getAnimation("player_die"));
        htReh.register();
    }

    private void switchStanding(SpriteShape shp) {
        if(shp.frames == standShape.frames) return;
        shp.maxFrames = standShape.maxFrames;
        shp.frames = standShape.frames;
        shp.timings = standShape.timings;
        shp.loop = standShape.loop;
        shp.currentFrame = 0;
        shp.currentTime = 0;
    }

    private void switchWalking(SpriteShape shp) {
        if(shp.frames == walkShape.frames) return;
        shp.maxFrames = walkShape.maxFrames;
        shp.frames = walkShape.frames;
        shp.timings = walkShape.timings;
        shp.loop = walkShape.loop;
        shp.currentFrame = 0;
        shp.currentTime = 0;
    }

    private void switchJumping(SpriteShape shp) {
        if(shp.frames == jumpShape.frames) return;
        shp.maxFrames = jumpShape.maxFrames;
        shp.frames = jumpShape.frames;
        shp.timings = jumpShape.timings;
        shp.loop = jumpShape.loop;
        shp.currentFrame = 0;
        shp.currentTime = 0;
    }

    private void switchHacking(SpriteShape shp) {
        if(shp.frames == hackShape.frames) return;
        shp.maxFrames = hackShape.maxFrames;
        shp.frames = hackShape.frames;
        shp.timings = hackShape.timings;
        shp.loop = hackShape.loop;
        shp.currentFrame = 0;
        shp.currentTime = 0;
    }
    
    private void switchPutAway(SpriteShape shp) {
        if(shp.frames == putAwayShape.frames) return;
        shp.maxFrames = putAwayShape.maxFrames;
        shp.frames = putAwayShape.frames;
        shp.timings = putAwayShape.timings;
        shp.loop = putAwayShape.loop;
        shp.currentFrame = 0;
        shp.currentTime = 0;
    }

    private void switchDie(SpriteShape shp) {
        if(shp.frames == dieShape.frames) return;
        shp.maxFrames = dieShape.maxFrames;
        shp.frames = dieShape.frames;
        shp.timings = dieShape.timings;
        shp.loop = dieShape.loop;
        shp.currentFrame = 0;
        shp.currentTime = 0;
    }

    public void update(long time) {
        int eid = player_eid;
        int textEid;
        PlayerInfo pi;
        WorldPhysics phys;
        SpriteShape shp;
        SpriteMovement mov;
        TextInfo ti;
        EndScreenInfo esi;
        boolean hasCoyoteTime;
        textEid = repo.findEntityWithComponent(TextInfo.class);
        pi = (PlayerInfo)repo.getComponent(eid, PlayerInfo.class);
        shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
        mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
        ti = textEid != EntityRepository.NO_ENTITY ?
            (TextInfo)repo.getComponent(textEid, TextInfo.class) :
            null;
        phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
        if(pi == null || shp == null || mov == null || phys == null) return;
        StageInfo info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
        if(info == null) return;
        if(pi.inputState == InputState.HACKING && ((pi.keyStatus & PlayerInfo.KEY_BACK) != 0)) {
            pi.inputState = InputState.MOVEMENT;
            switchPutAway(shp);
            if(ti != null) {
                ti.displayTime = ti.maxDisplayTime;
                ti.textBuffer.append(TextInfo.readyString);
            }
        } else if(pi.inputState == InputState.EXIT_LEVEL) {
            esi = (EndScreenInfo)repo.getComponent(eid, EndScreenInfo.class);
            if(esi == null) return;
            long timeElapsed = time - esi.endStageTime;
            if(esi.hiddenCollectiblesCollected > esi.collectiblesCollected) {
                esi.collectiblesCollected = (short)(timeElapsed / 100L);
            }
            if(esi.hiddenIntelCollected > esi.intelCollected) {
                esi.intelCollected = (short)(timeElapsed / 100L);
            }
            long starDelay = 500 + (esi.hiddenCollectiblesCollected * 100);
            if(esi.score < esi.hiddenScore) {
                esi.score = (timeElapsed < starDelay) ?
                    0 :
                    (short)((timeElapsed - starDelay) / 200L);
            }
        } else if(pi.inputState == InputState.MOVEMENT) {
            if((pi.keyStatus & PlayerInfo.KEY_HACK) != 0) {
                pi.flags &= (~PlayerInfo.JUMPED); // reset JUMPED flag  
                phys.gaccel = 0.f; // stop movement
                mov.velocity.x = 0.f;
                mov.acceleration.x = 0.f;
                pi.inputState = InputState.HACKING; // enter hacking state
                /// turn on display and show Lorn entering a command
                if(ti != null) {
                    ti.showDisplay = true;
                    ti.textBuffer.append(TextInfo.scanString);
                    ti.textBuffer.append(TextInfo.selectString);
                }
                switchHacking(shp);
            } else if((pi.keyStatus & PlayerInfo.KEY_RIGHT) != 0) {
                switch(phys.state) {
                case GROUNDED:
                    phys.gaccel = (mov.velocity.x < 0
                                   ? PhysicsUpdateAgent.BASE_FRIC * 1.2f
                                   : WALK_ACCEL);
                    phys.flags |= WorldPhysics.FACING_RIGHT;
                    switchWalking(shp);
                    break;
                case FALLING:
                    phys.thrust.x = 0.04f;
                    break;
                }
            }
            else if((pi.keyStatus & PlayerInfo.KEY_LEFT) != 0) {
                switch(phys.state) {
                case GROUNDED:
                    phys.gaccel = -(mov.velocity.x > 0
                                   ? PhysicsUpdateAgent.BASE_FRIC * 1.2f
                                   : WALK_ACCEL);
                    phys.flags &= ~WorldPhysics.FACING_RIGHT;
                    switchWalking(shp);
                    break;
                case FALLING:
                    phys.thrust.x = -0.04f;
                    break;
                }
            }
            else if(phys.state == WorldPhysics.State.GROUNDED) {
                phys.gaccel = 0.f;
                if(shp.frames != hackShape.frames &&
                   shp.frames != putAwayShape.frames) {
                        switchStanding(shp);
                }
            }
        
            // The JUMPED PlayerInfo flag tracks whether we've already
            // jumped for this jump key press. A jump will not be
            // triggered if the player is still holding the jump button
            // down from his last jump. This prevents Lorn from having
            // "moon boots" (i.e., he bounces continuously as long as the
            // jump button is held).
            hasCoyoteTime = ((phys.flags & WorldPhysics.COYOTE_TIME) != 0);
            if(((phys.state == WorldPhysics.State.GROUNDED) ||
                hasCoyoteTime)
               && ((pi.keyStatus & PlayerInfo.KEY_JUMP) != 0)
               && ((pi.flags & PlayerInfo.JUMPED) == 0)) {
                mov.position.y -= 2.0f;
                mov.velocity.y = -6.f;
                phys.gaccel = 0.f;
                mov.acceleration.x = 0.f;
                phys.state = WorldPhysics.State.FALLING;
                pi.flags |= PlayerInfo.JUMPED;
                switchJumping(shp);
            }
        
            // If the jump key is released, reset the JUMPED flag.

            if((pi.keyStatus & PlayerInfo.KEY_JUMP) == 0) {
                pi.flags &= (~PlayerInfo.JUMPED);
            }
        }
        shp.shapes = ((phys.flags & WorldPhysics.FACING_RIGHT) != 0) ? rightBitmap : leftBitmap;
        if((phys.flags & WorldPhysics.SHOULD_DESTROY) != 0 &&
           (pi.flags & PlayerInfo.DEAD) == 0) { die(pi, phys, mov, shp); }
        // update visibility of each hack target based on LOS to Lorn
        if(pi.inputState == InputState.HACKING) {
            htReh.processAll(htProc);
        }
        // have we reached the end door?
        pt1.set(mov.position);
        pt1.offset(-(float)info.goalX,-(float)info.goalY);
        if(pt1.length() <= phys.radius * 1.2) {
            switchStanding(shp);
            pi.keyStatus = 0;
            phys.thrust.set(0.f, 0.f);
            mov.velocity.set(0.f, 0.f);
            if(pi.inputState != InputState.EXIT_LEVEL) {
                // start door open sequence
                repo.processEntitiesWithComponent
                    (FinalDoorInfo.class,
                     (fdEid) -> {
                        FinalDoorInfo fdi = (FinalDoorInfo)repo.getComponent(fdEid, FinalDoorInfo.class);
                        fdi.state = DoorState.OPENING;
                    });
                // total up collectibles for end level screen
                esi = (EndScreenInfo)repo.getComponent(eid, EndScreenInfo.class);
                if(esi != null) {
                    final EndScreenInfo esi2 = esi;
                    repo.processEntitiesWithComponent
                        (CollectibleInfo.class,
                         (collEid) -> {
                            CollectibleInfo ci = (CollectibleInfo)repo.getComponent(collEid, CollectibleInfo.class);
                            switch(ci.type) {
                            case TRINKET:
                                esi2.collectiblesTotal++;
                                if(ci.state == CollectibleState.COLLECTED) {
                                    esi2.hiddenCollectiblesCollected++;
                                }
                                break;
                            case INTEL:
                                esi2.intelTotal++;
                                if(ci.state == CollectibleState.COLLECTED) {
                                    esi2.hiddenIntelCollected++;
                                }
                                break;
                            }
                        });
                    esi.hiddenScore = 1;
                    if(esi.hiddenCollectiblesCollected >= (esi.collectiblesTotal * 4 / 5)) {
                        esi.hiddenScore++;
                    }
                    if(esi.hiddenIntelCollected == esi.intelTotal) {
                        esi.hiddenScore++;
                    }
                    esi.endStageTime = time;
                }
            }
            pi.inputState = InputState.EXIT_LEVEL;
        }
    }

    public void die(PlayerInfo pi, WorldPhysics phys, SpriteMovement mov, SpriteShape shp) {
        pi.flags |= PlayerInfo.DEAD;
        pi.inputState = InputState.DEATH;
        phys.flags &= ~WorldPhysics.SOLID_COLLISION;
        switchDie(shp); 
        phys.thrust.set(0.f, 0.f);
        mov.velocity.set(0.f, -6.f);
        mov.acceleration.set(0.f, 0.f);
        phys.state = WorldPhysics.State.FALLING;
    }
}
