package org.foxesworld.animatix.animation.imageEffect.effects;

public enum Effects {
    RESIZE,
    MOVE,
    FADE,
    DECAY,
    BOUNCE,
    CRACK,
    COLOR_FADE,
    ROTATE;
    public static Effects fromString(String effect) {
        try {
            return Effects.valueOf(effect.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid effect type: " + effect);
        }
    }
}
