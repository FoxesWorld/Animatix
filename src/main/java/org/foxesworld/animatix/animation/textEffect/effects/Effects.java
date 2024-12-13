package org.foxesworld.animatix.animation.textEffect.effects;

public enum Effects {
    COLOR_CHANGE,
    ZOOM_IN;

    public static Effects fromString(String effect) {
        try {
            return Effects.valueOf(effect.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid effect type: " + effect);
        }
    }
}
