package org.foxesworld.animatix.animation.textEffect.effects;

import org.foxesworld.animatix.animation.imageEffect.effects.ImageEffects;

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
