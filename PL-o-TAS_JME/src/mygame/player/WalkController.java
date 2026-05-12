/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.player;

/**
 *
 * @author rodri
 */
public class WalkController implements PlayerController{
    private final Player player;
    private final MovementHandler movement;

    public WalkController(Player player) {
        this.player = player;
        PlayerAnimation anim = new PlayerAnimation(player.getModel());
        this.movement = new MovementHandler(player.getControl(), anim);
    }

    @Override
    public void update(InputPlayer input, float tpf, float cameraYaw) {
        movement.update(input, tpf, cameraYaw, player.isFirstPerson());
    }
}
