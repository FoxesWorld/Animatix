package org.foxesworld.animatix.animation.config;

import java.awt.*;
import java.util.List;

public class AnimationConfig {
    private List<ImageConfig> images;

    public static class ImageConfig {


        private String animationName;
        private Bounds bounds;
        private boolean repeat;
        private String imagePath;
        private List<AnimationPhase> phases;

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

    public List<ImageConfig> getImages() {
        return images;
    }
}