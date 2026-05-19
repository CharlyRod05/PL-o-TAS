package mygame.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import java.util.List;
import java.util.*;
import mygame.states.BuildState;
import mygame.states.GameState;

public class HUDDebug {

    private final SimpleApplication app;
    private final Node guiNode;
    private final BitmapFont font;

    private BitmapText debugText;

    public HUDDebug(Application app) {
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.font = this.app.getAssetManager()
                .loadFont("Interface/Fonts/Default.fnt");

        build();
    }

    private void build() {

        int h = app.getCamera().getHeight();

        debugText = new BitmapText(font);
        debugText.setSize(font.getCharSet().getRenderedSize());
        debugText.setColor(ColorRGBA.Cyan);

        // esquina superior izquierda
        debugText.setLocalTranslation(10, h - 10, 0);

        guiNode.attachChild(debugText);
    }

    public void update(GameState gameState) {

        StringBuilder sb = new StringBuilder();

        sb.append("=== DEBUG HUD ===\n");

        // FPS
        sb.append("FPS: ")
          .append(app.getTimer().getFrameRate())
          .append("\n\n");

        // LEVEL
        sb.append("Level: ");

        if (gameState.getCurrentLevel() != null) {
            sb.append(gameState.getCurrentLevel().getLevelName());
        } else {
            sb.append("NULL");
        }

        sb.append("\n");

        // PLAYER POSITION
        sb.append("Player Pos: ");

        if (gameState.getPlayer() != null) {
            sb.append(gameState.getPlayer()
                    .getPosition());
        } else {
            sb.append("NULL");
        }

        sb.append("\n");

        // CAMERA
        sb.append("Camera: ");

        if (gameState.isFirstPerson()) {
            sb.append("FIRST PERSON");
        } else {
            sb.append("THIRD PERSON");
        }

        sb.append("\n");

        // CURRENT CONTROLLER
        sb.append("Controller: ");

        if (gameState.getCurrentController() != null) {
            sb.append(gameState.getCurrentController()
                    .getClass()
                    .getSimpleName());
        } else {
            sb.append("NULL");
        }

        sb.append("\n\n");

        // STATES
        sb.append("Attached States:\n");

        sb.append(gameState.getAttachedStatesDebug());

        sb.append("\n");

        // BUILD STATE INFO
        if (gameState.getBuildState() != null) {

            sb.append("Build Mode: ")
              .append(gameState.getBuildState().isBuildMode())
              .append("\n");

            int[] remaining =
                    gameState.getBuildState().getRemaining();

            sb.append("Remaining Pieces:\n");
            sb.append(" Flat: ").append(remaining[0]).append("\n");
            sb.append(" Ramp: ").append(remaining[1]).append("\n");
            sb.append(" Bounce: ").append(remaining[2]).append("\n");
            sb.append(" Funnel: ").append(remaining[3]).append("\n");
            
            sb.append("TIPO BUILD \n");
            sb.append(gameState.getBuildState().getSelectedType());
        }

        debugText.setText(sb.toString());
    }
}