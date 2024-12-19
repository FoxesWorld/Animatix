package org.foxesworld.animatix.animation.config;

import java.awt.*;
import java.util.List;

public class Animation {
    private String name, type, imagePath, text;
    private Bounds bounds;
    private boolean repeat, visible;
    private List<Phase> phases;

    public String getName() {
        return name;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getText() {
        return text;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public Rectangle getBounds() {
        return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public static class Bounds {
        private int x, y, width, height;

        public Bounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}