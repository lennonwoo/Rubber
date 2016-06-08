package com.lennonwoo.rubber.utils;

import java.util.logging.Handler;

public class somethingUseful {

    private static final int ROTATE_VELOCITY = 10;
    private static final int ROTATE_DELAY = 10;
    private boolean beRotating;
    private Handler rotateHandler;
    private int currentRotateDegree;
    private final Runnable runnableRotate = new Runnable() {
        @Override
        public void run() {
            if (beRotating) {
                currentRotateDegree += ROTATE_VELOCITY;
                currentRotateDegree %= 360;
//                rotateHandler.postDelayed(runnableRotate, ROTATE_DELAY);
            }
        }
    };
}
