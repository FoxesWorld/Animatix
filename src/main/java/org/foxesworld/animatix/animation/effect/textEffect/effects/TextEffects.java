package org.foxesworld.animatix.animation.effect.textEffect.effects;

public enum TextEffects {
    FADE,
    SLIDE;

    public static TextEffects fromString(String effect) {
        try {
            return TextEffects.valueOf(effect.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid effect type: " + effect);
        }
    }
}
