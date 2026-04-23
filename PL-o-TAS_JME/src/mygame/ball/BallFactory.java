/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.ball;

import com.jme3.asset.AssetManager;

/**
 *
 * @author rodri
 */
public class BallFactory {
    private final AssetManager assetManager;

    public BallFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public AbstractBall create(BallType type) {
        switch (type) {
            case BASIC:
                return new BasicBall(assetManager);
            // case HEAVY:
            //     return new HeavyBall(assetManager);
            // case BOUNCY:
            //     return new BouncyBall(assetManager);
            default:
                throw new IllegalArgumentException(
                    "BallFactory: tipo de pelota desconocido → " + type);
        }
    }
}
