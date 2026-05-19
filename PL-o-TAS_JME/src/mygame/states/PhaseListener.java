package mygame.states;

public interface PhaseListener {
    void onBuildComplete();
    void onPlayResult(PhaseResult result);
}
