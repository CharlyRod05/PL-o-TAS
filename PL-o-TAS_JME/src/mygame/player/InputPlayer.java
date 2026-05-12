/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.player;

/**
 *
 * @author rodri
 */
public class InputPlayer {
    public boolean forward;
    public boolean backward;
    public boolean left;
    public boolean right;
    public boolean jump;
    public boolean buildMode;
    public boolean placeBlock;
    public boolean breakBlock;

    public void reset() {
        //forward = backward = left = right = false;
        jump = false;
        buildMode = false;
        placeBlock = false;
        breakBlock = false;
    }
}
