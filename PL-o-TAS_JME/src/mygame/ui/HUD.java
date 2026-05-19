package mygame.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import mygame.level.Level;

public class HUD {
    private final SimpleApplication app;
    private final Node guiNode;
    private final BitmapFont font;

    private BitmapText levelLabel;
    private BitmapText counterLabel;
    private BitmapText modeLabel;
    private BitmapText hintLabel;
    private BitmapText messageLabel;

    public HUD(Application app) {
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.font = this.app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        buildLabels();
    }

    private void buildLabels() {
        int w = app.getCamera().getWidth();
        int h = app.getCamera().getHeight();

        levelLabel   = makeText(ColorRGBA.White,     20,          h - 20);
        counterLabel = makeText(ColorRGBA.White,     w - 320,     h - 20);
        modeLabel    = makeText(ColorRGBA.Yellow,    w / 2f - 100, 60);
        hintLabel    = makeText(ColorRGBA.LightGray, w / 2f - 100, 30);
        messageLabel = makeText(ColorRGBA.Green,     w / 2f - 150, h / 2f);
        messageLabel.setSize(font.getCharSet().getRenderedSize() * 2);

        guiNode.attachChild(levelLabel);
        guiNode.attachChild(counterLabel);
        guiNode.attachChild(modeLabel);
        guiNode.attachChild(hintLabel);
        guiNode.attachChild(messageLabel);

        clearAll();
    }

    private BitmapText makeText(ColorRGBA color, float x, float y) {
        BitmapText t = new BitmapText(font, false);
        t.setSize(font.getCharSet().getRenderedSize());
        t.setColor(color);
        t.setLocalTranslation(x, y, 0);
        return t;
    }

    private void clearAll() {
        levelLabel.setText("");
        counterLabel.setText("");
        modeLabel.setText("");
        hintLabel.setText("");
        messageLabel.setText("");
    }

    public void showBuildHUD(Level level, int[] remaining, boolean buildMode) {
        clearAll();
        levelLabel.setText(level.getLevelName());
        updateBuildCounter(remaining, buildMode);
        hintLabel.setText("B - Cambiar modo  |  ENTER - Soltar pelota");
    }

    public void updateBuildCounter(int[] remaining, boolean buildMode) {
        counterLabel.setText(String.format(
            "Planas: %d  Rampas: %d  Rebote: %d  Embudo: %d",
            remaining[0], remaining[1], remaining[2], remaining[3]));
        modeLabel.setText(buildMode ? "CONSTRUYENDO" : "CAMINANDO");
    }

    public void showPlayHUD() {
        clearAll();
    }

    public void showRetryPrompt() {
        clearAll();
        hintLabel.setText("R - Reintentar");
    }

    public void showCompletedMessage() {
        clearAll();
        messageLabel.setColor(ColorRGBA.Green);
        messageLabel.setText("¡Completado!");
        hintLabel.setText("Volviendo al Nivel 1...");
    }
}
