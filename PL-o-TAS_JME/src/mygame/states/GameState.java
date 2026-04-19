/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import mygame.player.Player;
import mygame.world.World;



public class GameState extends BaseAppState {
    private SimpleApplication app;
    World world;
    Player player;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        System.out.println("GameState inicializado");
        //crea world
        world = new World(app);
        this.app.getRootNode().attachChild(world.getNode());
        //creaPersonaje
        player = new Player(app,world.getPhysics());
        this.app.getRootNode().attachChild(player.getNode());
    }

    @Override
    public void update(float tpf) {
        // lógica por frame (aún vacío)
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
