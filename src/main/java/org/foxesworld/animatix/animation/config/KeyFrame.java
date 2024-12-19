package org.foxesworld.animatix.animation.config;

import java.util.List;

public class KeyFrame {
    private List<Effect> effects;
    private double alpha;
    private int delay, duration, x,y, width, height;

    public double getAlpha() {
        return alpha;
    }

    public int getDelay() {
        return delay;
    }

    public int getDuration() {
        return duration;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
