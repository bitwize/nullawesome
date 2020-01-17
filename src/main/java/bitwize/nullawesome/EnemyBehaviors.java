package bitwize.nullawesome;

public final class EnemyBehaviors {
    public static EntityProcessor groundPatrol = (eid) -> {
        StageInfo info;
        EntityRepository repo = EntityRepository.get();
        WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
        EnemyInfo ei = (EnemyInfo)repo.getComponent(eid, EnemyInfo.class);
        SpriteMovement mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
        if(ei == null || mov == null || phys == null) return;
        if(phys.state != WorldPhysics.State.GROUNDED) return;
        info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
        if(info == null) return;
        TileMap map = info.map;
        phys.gvelmax = ei.walkVel;
        float frontSensor = mov.position.x;
        if((phys.flags & WorldPhysics.FACING_RIGHT) != 0) {
            frontSensor += phys.radius;
        } else {
            frontSensor -= phys.radius;
        }
        boolean shouldTurnAround = ((map.getTileFlags(map.getTileWorldCoords(frontSensor, mov.position.y + phys.radius)) &
                TileMap.FLAG_SOLID) == 0) ||
                ((map.getTileFlags(map.getTileWorldCoords(frontSensor, mov.position.y))) != 0);
        if(shouldTurnAround) {
            phys.thrust.x = 0;
            phys.gaccel = 0;
            mov.velocity.x = 0;
            mov.acceleration.x = 0;
            phys.flags ^= WorldPhysics.FACING_RIGHT;
        }
        if((phys.flags & WorldPhysics.FACING_RIGHT) != 0) {
            phys.gaccel = 0.2f;
        } else {
            phys.gaccel = -0.2f;
        }
    };
    public static EntityProcessor chase = (eid) -> {
        StageInfo info;
        EntityRepository repo = EntityRepository.get();
        WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
        EnemyInfo ei = (EnemyInfo)repo.getComponent(eid, EnemyInfo.class);
        SpriteMovement mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
        if(ei == null || mov == null || phys == null) return;
        if(phys.state != WorldPhysics.State.GROUNDED) return;
        info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
        if(info == null) return;
        if(ei.targetEid == EntityRepository.NO_ENTITY) return;
        TileMap map = info.map;
        SpriteMovement tmov = (SpriteMovement)repo.getComponent(ei.targetEid, SpriteMovement.class);
        ei.targetLastKnownPosition.set(tmov.position);
        phys.gvelmax = ei.chaseVel;
        if(tmov.position.x >= mov.position.x) {
            phys.flags |= WorldPhysics.FACING_RIGHT;
            phys.gaccel=0.3f;
        } else {
            phys.flags &= ~(WorldPhysics.FACING_RIGHT);
            phys.gaccel=-0.3f;
        }
    };

    public static Criterion seesTargetCriterion = (eid) -> {
        EntityRepository repo = EntityRepository.get();
        EnemyInfo ei = (EnemyInfo)repo.getComponent(eid, EnemyInfo.class);
        if(ei == null) {
            return false;
        }
        return (ei.flags & EnemyInfo.SEES_TARGET) != 0;
    };

    public static Criterion doesntSeeTargetCriterion = (eid) -> {
        return !(seesTargetCriterion.test(eid));
    };
}
