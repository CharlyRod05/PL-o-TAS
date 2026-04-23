/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.physics;

import com.jme3.bullet.collision.PhysicsCollisionObject;

/**
 *
 * @author rodri
 */
public final class CollisionGroups {
    private CollisionGroups() {} // no instanciable

    public static final int WORLD = PhysicsCollisionObject.COLLISION_GROUP_01;
    public static final int BALL  = PhysicsCollisionObject.COLLISION_GROUP_02;
}
