package org.foxesworld.animatix.animation.imageEffect.effects;

public enum ImageEffects {
    RESIZE,
    MOVE,
    DECAY,
    BOUNCE,
    SPIN,
    CRACK,
    COLORFADE,
    FADE,
    BORDERHIGHLIGHT,
    ROTATE;
    public static ImageEffects fromString(String effect) {
        try {
            return ImageEffects.valueOf(effect.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid effect type: " + effect);
        }
    }
}
