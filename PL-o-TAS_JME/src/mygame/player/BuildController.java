/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.player;

/**
 *
 * @author rodri
 */
public class BuildController implements PlayerController{
    private final Player player;
    private final MovementHandler movement;

    public BuildController(Player player) {
        this.player = player;
        PlayerAnimation anim = new PlayerAnimation(player.getModel());
        this.movement = new MovementHandler(player.getControl(), anim);
    }

    @Override
    public void update(InputPlayer input, float tpf, float cameraYaw) {
        // Movimiento normal heredado
        movement.update(input, tpf, cameraYaw, player.isFirstPerson());

        // Acciones de construcción (por ahora solo imprimen)
        if (input.placeBlock) {
            System.out.println("Colocar bloque en posición: " + player.getPosition());
        }
        if (input.breakBlock) {
            System.out.println("Romper bloque");
        }
    }
}
