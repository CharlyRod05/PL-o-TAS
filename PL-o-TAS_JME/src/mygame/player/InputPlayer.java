package mygame.player;

public class InputPlayer {
    public boolean forward, backward, left, right, jump;
    public boolean buildMode;
    public boolean placeBlock;
    public boolean breakBlock;
    public boolean buildToggle;
    public boolean cycleType;
    public boolean scrollUp;
    public boolean scrollDown;
    public boolean releaseBall;
    public boolean retry;
    public boolean place;
    public boolean remove;

    public void reset() {
        jump = buildMode = placeBlock = breakBlock = false;
        buildToggle = cycleType = scrollUp = scrollDown = false;
        releaseBall = retry = place = remove = false;
    }
}
