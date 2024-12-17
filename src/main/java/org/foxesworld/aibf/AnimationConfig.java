package org.foxesworld.aibf;

import java.util.Map;

public class AnimationConfig {

    private Meta meta;
    private String imagePath;
    private AnimationPhase[] phases;

    // Getters и Setters

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public AnimationPhase[] getPhases() {
        return phases;
    }

    public void setPhases(AnimationPhase[] phases) {
        this.phases = phases;
    }

    // Подмодели
    public static class Meta {
        private String animationName;
        private String author;
        private int fps;

        // Getters и Setters

        public String getAnimationName() {
            return animationName;
        }

        public void setAnimationName(String animationName) {
            this.animationName = animationName;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getFps() {
            return fps;
        }

        public void setFps(int fps) {
            this.fps = fps;
        }
    }

    public static class AnimationPhase {
        private String name;
        private int duration;
        private String effectType;
        private Map<String, Object> params;

        // Getters и Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getEffectType() {
            return effectType;
        }

        public void setEffectType(String effectType) {
            this.effectType = effectType;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }
}
