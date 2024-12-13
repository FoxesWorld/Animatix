package org.example.animation.config;

import java.util.List;

public class AnimationConfig {
    private String animationName;
    private boolean repeat;
    private String imagePath;
    private List<AnimationPhase> phases;

    // Getters и Setters
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAnimationName() {
        return animationName;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public List<AnimationPhase> getPhases() {
        return phases;
    }
}