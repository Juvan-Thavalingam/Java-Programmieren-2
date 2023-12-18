package ch.zhaw.prog2.bridge;

import java.awt.*;

public class Car implements Runnable {

    public static final int REDCAR  = 0;
    public static final int BLUECAR = 1;

    private static final int BRIDGE_Y = 95;
    private static final int BRIDGE_X_LEFT = 210;
    private static final int BRIDGE_X_LEFT2 = 290;
    private static final int BRIDGE_X_MID = 410;
    private static final int BRIDGE_X_RIGHT2 = 530;
    private static final int BRIDGE_X_RIGHT = 610;
    private static final int TOTAL_WIDTH = 900;
    private static final int[] INIT_X = {-80, TOTAL_WIDTH};
    private static final int[] INIT_Y = {135, 55};
    private static final int OUT_LEFT = -200;
    private static final int OUT_RIGHT = TOTAL_WIDTH + 200;

    final int cartype;
    int xpos, ypos;
    final Car inFront;
    final Image image;
    final TrafficController controller;

    public Car(int cartype, Car inFront, Image image, TrafficController controller) {
        this.cartype = cartype;
        this.inFront = inFront;
        this.image = image;
        this.controller  = controller;
        if (cartype == REDCAR) {
            xpos = inFront == null ? OUT_RIGHT : Math.min(INIT_X[cartype], inFront.getX()-90);
        } else {
            xpos = inFront == null ? OUT_LEFT : Math.max(INIT_X[cartype], inFront.getX()+90);
        }
        ypos = INIT_Y[cartype];
    }

    public void move() {
        int xposOld =  xpos;
        if (cartype==REDCAR) {
            if (inFront.getX() - xpos > 100) {
                xpos += 4;
                if (xpos >= BRIDGE_X_LEFT && xposOld < BRIDGE_X_LEFT) controller.enterLeft();
                else if (xpos > BRIDGE_X_LEFT && xpos < BRIDGE_X_MID) { if (ypos > BRIDGE_Y) ypos -= 2; }
                else if (xpos >= BRIDGE_X_RIGHT2 && xpos < BRIDGE_X_RIGHT) { if (ypos < INIT_Y[REDCAR]) ypos += 2; }
                else if (xpos >= BRIDGE_X_RIGHT &&  xposOld < BRIDGE_X_RIGHT) controller.leaveRight();
            }
        } else {
            if (xpos-inFront.getX() > 100) {
                xpos -= 4;
                if (xpos <= BRIDGE_X_RIGHT && xposOld > BRIDGE_X_RIGHT) controller.enterRight();
                else if (xpos < BRIDGE_X_RIGHT && xpos > BRIDGE_X_MID) { if (ypos < BRIDGE_Y) ypos += 2; }
                else if (xpos <= BRIDGE_X_LEFT2 && xpos > BRIDGE_X_LEFT) { if(ypos > INIT_Y[BLUECAR]) ypos -= 2; }
                else if (xpos <= BRIDGE_X_LEFT && xposOld > BRIDGE_X_LEFT) controller.leaveLeft();
            }
        }
    }


    public void run() {
        boolean outOfSight = cartype == REDCAR ? xpos > TOTAL_WIDTH : xpos < -80;
        while (!outOfSight) {
            move();
            outOfSight = cartype == REDCAR ? xpos > TOTAL_WIDTH : xpos < -80;
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        xpos = cartype == REDCAR ? OUT_RIGHT : OUT_LEFT;
    }

    public int getX() { return xpos; }

    public void draw(Graphics g) {
        g.drawImage(image, xpos, ypos, null);
    }
}
