package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.EnumMap;

public class EnemyUpdateAgent implements UpdateAgent {
    private static EnumMap<EnemyType, Bitmap> enemyImagesL = new EnumMap<EnemyType, Bitmap>(EnemyType.class);
    private static EnumMap<EnemyType, Bitmap> enemyImagesR = new EnumMap<EnemyType, Bitmap>(EnemyType.class);
    private static EnumMap<EnemyType, SpriteShape> enemyStandAnims =
                        new EnumMap<EnemyType, SpriteShape>(EnemyType.class);
        private static EnumMap<EnemyType, SpriteShape> enemyWalkAnims =
                        new EnumMap<EnemyType, SpriteShape>(EnemyType.class);
        private static Bitmap shockRayL, shockRayR;
    private static final EnemyType[] enemyTypes = EnemyType.values();
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();
    private static PointF march = new PointF();
    private RelevantEntitiesHolder ereh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(EnemyInfo.class));
        private RelevantEntitiesHolder preh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(EnemyProjectileInfo.class));
        private ArrayList<Integer> entsToRemove = new ArrayList<Integer>();
    private EntityProcessor proc = (eid) -> {
        EnemyInfo ei = (EnemyInfo) repo.getComponent(eid, EnemyInfo.class);
        SpriteMovement mv = (SpriteMovement) repo.getComponent(eid, SpriteMovement.class);
        WorldPhysics phys = (WorldPhysics) repo.getComponent(eid, WorldPhysics.class);
        SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
        if(ei == null || mv == null || phys == null || shp == null) return;
        StageInfo info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
        if(info == null) return;
        TileMap map = info.map;
        if(ei.fireTimer > 0) ei.fireTimer--;
        if((phys.flags & WorldPhysics.FACING_RIGHT) != 0) {
            shp.shapes = enemyImagesR.get(ei.type);
        } else {
            shp.shapes = enemyImagesL.get(ei.type);
        }
        if(Math.abs(mv.velocity.x) <= 0.001) {
                SpriteShape.changeAnimation(shp, enemyStandAnims.get(ei.type));
        } else {
                SpriteShape.changeAnimation(shp, enemyWalkAnims.get(ei.type));
        }
        if(ei.targetEid != EntityRepository.NO_ENTITY) {
                SpriteMovement mvTarget = (SpriteMovement) repo.getComponent(ei.targetEid, SpriteMovement.class);
                if (mvTarget != null) {
                    boolean st = seesTarget(mv, mvTarget, map, ei.sightRange, ei.sightFrustum, (phys.flags & WorldPhysics.FACING_RIGHT) != 0, ei.flags, ei.currentState);
                        if(st) {
                                ei.flags |= EnemyInfo.SEES_TARGET;
                        } else {
                                ei.flags &= ~(EnemyInfo.SEES_TARGET);
                        }
                }
        }
        EntityProcessor action = ei.stateActions.get(ei.currentState);
        if(action != null) {
            action.process(eid);
        }
        EnemyStateTransition[] xions = ei.script.get(ei.currentState);
        if(xions != null) {
            for(EnemyStateTransition xion : xions) {
                if(xion.criterion.test(eid)) {
                    ei.currentState = xion.newState;
                    break;
                }
            }
        }
    };
        private static final int SHOCKRAY_XDISPLACE = 80;
        private static final int SHOCKRAY_YDISPLACE = -8;
        private static final int LASERBOLT_XDISPLACE = 24;
        private static final int LASERBOLT_YDISPLACE = -2;

    private EntityProcessor pproc = (eid2) -> {
                EnemyProjectileInfo pi = (EnemyProjectileInfo) repo.getComponent(eid2, EnemyProjectileInfo.class);
                SpriteMovement mv = (SpriteMovement) repo.getComponent(eid2, SpriteMovement.class);
                WorldPhysics phys = (WorldPhysics) repo.getComponent(eid2, WorldPhysics.class);
                SpriteShape shp = (SpriteShape)repo.getComponent(eid2, SpriteShape.class);
                if(pi == null || mv == null || phys == null || shp == null) return;
        switch(pi.type) {
                        case SHOCK_RAY: {
                                SpriteMovement shooterMv = (SpriteMovement) repo.getComponent(pi.shotByEid, SpriteMovement.class);
                                WorldPhysics shooterPhys = (WorldPhysics) repo.getComponent(pi.shotByEid, WorldPhysics.class);
                                boolean shooterFacingRight = ((shooterPhys.flags & WorldPhysics.FACING_RIGHT) != 0);
                                if (shooterMv == null || phys == null) break;
                                mv.position.x = shooterMv.position.x + (shooterFacingRight
                                                ? SHOCKRAY_XDISPLACE
                                                : -SHOCKRAY_XDISPLACE);
                                mv.position.y = shooterMv.position.y + SHOCKRAY_YDISPLACE;
                                mv.zOrder = 0;
                                if(shooterFacingRight) {
                                        phys.flags |= WorldPhysics.FACING_RIGHT;
                                } else {
                                        phys.flags &= ~WorldPhysics.FACING_RIGHT;
                                }
                                shp.shapes = shooterFacingRight ? shockRayR : shockRayL;
                                break;
                        }
                        case LASER_BOLT: {
                                mv.acceleration.set(0.f, 0.f);
                                break;
                        }
                }if(pi.lifetime <= 0) {
                        entsToRemove.add(eid2);
                        return;
                }
                pi.lifetime--;
        };

    private static boolean seesTarget(SpriteMovement mvViewer, SpriteMovement mvTarget, TileMap map, float range, float frustAng, boolean facingRight, int flags, EnemyState state) {
        float distX = mvTarget.position.x - mvViewer.position.x;
        float distY = mvTarget.position.y - mvViewer.position.y;
        if(((distX * distX) + (distY * distY)) > range * range) {
                return false;
        }
        if((flags & EnemyInfo.LIDAR) == 0 &&
           state != EnemyState.ATTACKING) {
            if(facingRight && distX < 0) {
                return false;
            }
            if((!facingRight) && distX > 0) {
                return false;
            }
            if (Math.abs(Math.atan2(Math.abs(distY), Math.abs(distX))) > frustAng) {
                return false;
            }
        }
        if((flags & EnemyInfo.IRVISION)==0) {
            if(!hasLineOfSight(mvViewer.position, mvTarget.position, map)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasLineOfSight(PointF start, PointF end, TileMap map) {
        march.set(start);
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
        dx *= TileMap.TILE_SIZE;
        dx /= dist;
        dy *= TileMap.TILE_SIZE;
        dy /= dist;
        for(float f=0; f<=dist; f += TileMap.TILE_SIZE) {
            if((map.getTileFlags(map.getTileWorldCoords(march.x, march.y)) & TileMap.FLAG_SOLID) != 0) {
                return false;
            }
            march.x += dx;
            march.y += dy;
        }
        return true;
    }

        public static int createShockRay(int stageEid, int shooterEid)
                        throws EntityTableFullException
        {
                EntityRepository repo = EntityRepository.get();
                int eid = repo.newEntity();
                SpriteShape shp = SpriteShape.loadAnimation(ContentRepository.get().getAnimation("shock_ray_anim"));
                shp.subsection = new Rect(0, 0, 32, 32);
                SpriteMovement mv = new SpriteMovement();
                WorldPhysics phys = new WorldPhysics();
                EnemyProjectileInfo pi = new EnemyProjectileInfo();
                EnemyCollider ec = new EnemyCollider();
                phys.stageEid = stageEid;
                phys.state = WorldPhysics.State.FALLING;
                phys.radius = 64;
                phys.hitbox.left = -64.f;
                phys.hitbox.top = -8.f;
                phys.hitbox.right = 64.f;
                phys.hitbox.bottom = 8.f;
                phys.flags = WorldPhysics.FACING_RIGHT;
                phys.collider = ec;
                phys.collisionCriterion = (cEid) -> EntityRepository.get()
                                .getComponent(cEid, PlayerInfo.class) != null;
                mv.position.set(0.f, 0.f);
                mv.hotspot.set(64.f, 8.f);
                pi.shotByEid = shooterEid;
                pi.type = EnemyProjectileType.SHOCK_RAY;
                pi.lifetime = 120;
                repo.addComponent(eid, shp);
                repo.addComponent(eid, mv);
                repo.addComponent(eid, phys);
                repo.addComponent(eid, pi);
                repo.addComponent(eid, ec);
                return eid;
        }

        public static int createLaserBolt(int stageEid, int shooterEid)
                        throws EntityTableFullException
        {
                EntityRepository repo = EntityRepository.get();
                int eid = repo.newEntity();
                SpriteShape shp = new SpriteShape();
                shp.shapes = ContentRepository.get().getBitmap("laser_bolt");
                shp.subsection = new Rect(0, 0, 16, 4);
                SpriteMovement mv = new SpriteMovement();
                WorldPhysics phys = new WorldPhysics();
                EnemyProjectileInfo pi = new EnemyProjectileInfo();
                EnemyCollider ec = new EnemyCollider();
                WorldPhysics shooterPhys = (WorldPhysics)EntityRepository.get().getComponent(shooterEid, WorldPhysics.class);
                SpriteMovement shooterMov = (SpriteMovement)EntityRepository.get().getComponent(shooterEid, SpriteMovement.class);
                phys.stageEid = stageEid;
                phys.state = WorldPhysics.State.FALLING;
                phys.radius = 8;
                phys.hitbox.left = -8.f;
                phys.hitbox.top = -2.f;
                phys.hitbox.right = 8.f;
                phys.hitbox.bottom = 2.f;
                phys.flags = WorldPhysics.FACING_RIGHT;
                phys.collider = ec;
                phys.collisionCriterion = (cEid) -> EntityRepository.get()
                                .getComponent(cEid, PlayerInfo.class) != null;
                mv.position.set(shooterMov.position.x + LASERBOLT_XDISPLACE, shooterMov.position.y + LASERBOLT_YDISPLACE);
                mv.hotspot.set(8.f, 2.f);
                mv.velocity.set((shooterPhys.flags & WorldPhysics.FACING_RIGHT) != 0 ? 2.f : -2.f, 0.f);
                pi.shotByEid = shooterEid;
                pi.type = EnemyProjectileType.LASER_BOLT;
                pi.lifetime = 120;
                repo.addComponent(eid, shp);
                repo.addComponent(eid, mv);
                repo.addComponent(eid, phys);
                repo.addComponent(eid, pi);
                return eid;
        }

    public static void fire(int stageEid, int shooterEid) {
        EnemyInfo ei = (EnemyInfo)EntityRepository.get().getComponent(shooterEid, EnemyInfo.class);
                if(ei.fireTimer > 0) return;
                ei.fireTimer = ei.fireCooldown;
                try {
                        switch (ei.type) {
                                case SENTRY_DRONE:
                                        createShockRay(stageEid, shooterEid);
                                        break;
                                case SOLDIER:
                                        createLaserBolt(stageEid, shooterEid);
                                        break;
                        }
                } catch(EntityTableFullException e) {
                        throw new RuntimeException(e);
                }
        }

    public EnemyUpdateAgent() {
        for(EnemyType etype : enemyTypes) {
            enemyImagesL.put(etype, content.getBitmap(etype.name().toLowerCase() + "_l"));
            enemyImagesR.put(etype, content.getBitmap(etype.name().toLowerCase() + "_r"));
            shockRayL = content.getBitmap("shock_ray_l");
            shockRayR = content.getBitmap("shock_ray_r");
            enemyStandAnims.put(etype,
                                SpriteShape.loadAnimation(content.getAnimation(etype.name().toLowerCase() +
                                "_stand")));
                enemyWalkAnims.put(etype,
                                SpriteShape.loadAnimation(content.getAnimation(etype.name().toLowerCase() +
                                                "_walk")));
        }
        ereh.register();
        preh.register();
    }

    public void update(long time) {
        ereh.processAll(proc);
        preh.processAll(pproc);
        if(!entsToRemove.isEmpty()) {
                for (int i : entsToRemove) {
                        repo.removeEntity(i);
                }
                entsToRemove.clear();
        }
    }
}
