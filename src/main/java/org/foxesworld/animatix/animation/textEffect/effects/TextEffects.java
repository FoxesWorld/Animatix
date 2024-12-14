package org.foxesworld.animatix.animation.textEffect.effects;

public enum TextEffects {
    COLOR_CHANGE,
    ZOOM_IN;

    public static TextEffects fromString(String effect) {
        try {
            return TextEffects.valueOf(effect.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid effect type: " + effect);
        }
    }
}
