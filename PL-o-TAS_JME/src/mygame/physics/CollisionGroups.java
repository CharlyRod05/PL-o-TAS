package mygame.physics;

import com.jme3.bullet.collision.PhysicsCollisionObject;

public final class CollisionGroups {
    private CollisionGroups() {}

    public static final int WORLD    = PhysicsCollisionObject.COLLISION_GROUP_01;
    public static final int BALL     = PhysicsCollisionObject.COLLISION_GROUP_02;
    public static final int PLATFORM = PhysicsCollisionObject.COLLISION_GROUP_03;
    public static final int OBSTACLE = PhysicsCollisionObject.COLLISION_GROUP_04;
}
