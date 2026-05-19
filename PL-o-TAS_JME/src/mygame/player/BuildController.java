package mygame.player;

public class BuildController implements PlayerController {
    private final MovementHandler movement;

    public BuildController(Player player) {
        PlayerAnimation anim = new PlayerAnimation(player.getModel());
        this.movement = new MovementHandler(player.getControl(), anim);
    }

    @Override
    public void update(InputPlayer input, float tpf, float cameraYaw) {
        movement.update(input, tpf, cameraYaw, true);
    }
}
