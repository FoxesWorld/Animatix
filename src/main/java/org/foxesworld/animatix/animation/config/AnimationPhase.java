package org.foxesworld.animatix.animation.config;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeType;

import java.util.List;

public class AnimationPhase {
    private List<String> imageEffects, textEffects;
    private String name;
    private int startWidth, endWidth, startHeight, endHeight, pixelDecaySpeed;
    private int startX, endX, startY, endY;
    private float startAlpha, endAlpha;
    private float startAngle, endAngle;
    private boolean bounce;
    private ResizeType resizeType;
    private long duration;
    private List<Vector2D> controlPoints; // Контрольные точки для кривой Безье

    // Геттеры и сеттеры
    public List<String> getImageEffects() {
        return imageEffects;
    }

    public List<String> getTextEffects() {
        return textEffects;
    }

    public int getStartWidth() {
        return startWidth;
    }

    public void setStartWidth(int startWidth) {
        this.startWidth = startWidth;
    }

    public int getEndWidth() {
        return endWidth;
    }

    public void setEndWidth(int endWidth) {
        this.endWidth = endWidth;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public void setStartHeight(int startHeight) {
        this.startHeight = startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public void setEndHeight(int endHeight) {
        this.endHeight = endHeight;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public float getStartAlpha() {
        return startAlpha;
    }

    public void setStartAlpha(float startAlpha) {
        this.startAlpha = startAlpha;
    }

    public float getEndAlpha() {
        return endAlpha;
    }

    public void setEndAlpha(float endAlpha) {
        this.endAlpha = endAlpha;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

    public ResizeType getResizeType() {
        return resizeType;
    }

    public void setResizeType(ResizeType resizeType) {
        this.resizeType = resizeType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<Vector2D> getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(List<Vector2D> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public int getPixelDecaySpeed() {
        return pixelDecaySpeed;
    }

    public boolean isBounce() {
        return bounce;
    }

    public String getName() {
        return name;
    }
}
