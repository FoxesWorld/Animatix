package org.foxesworld.animatix.animation.config.attributes;

import java.awt.*;
import java.util.List;

public class KeyFrame {
    private List<Effect> effects;
    private double alpha;
    private int fontSize;
    private int delay;
    private int duration;
    private int x, y, width, height;
    private String font;
    private String textColor;

    public KeyFrame(List<Effect> effects, double alpha, int fontSize, int delay, int duration, int x, int y, int width, int height, String font, String textColor) {
        this.effects = effects;
        this.alpha = alpha;
        this.fontSize = fontSize;
        this.delay = delay;
        this.duration = duration;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.font = font;
        this.textColor = textColor;
    }

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

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getFont() {
        return font;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
