package org.example.animation.imageEffect.effects;

public enum Effects {
    RESIZE,
    MOVE,
    FADE,
    DECAY,
    BOUNCE,
    ROTATE;

    // Utility method to get Effects from a string, if needed
    public static Effects fromString(String effect) {
        try {
            return Effects.valueOf(effect.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid effect type: " + effect);
        }
    }
}
