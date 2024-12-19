package org.foxesworld.animatix.animation.config;

import org.foxesworld.animatix.AnimationFactory;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class KeyFrame {
    private List<Effect> effects;
    private double alpha;
    private int delay, duration, x,y, width, height;

    public double getAlpha() {
        return alpha;
    }

    public int getDelay() {
        return delay;
    }

    public int getDuration() {
        return duration;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public <T> T getEffectParam(String effectType, String paramName, Class<T> clazz) {
        if (effects == null) {
            AnimationFactory.logger.log(System.Logger.Level.WARNING, "Effects list is null");
            return null;
        }

        for (Effect effect : effects) {
            if (effect.getType().equals(effectType)) {
                Map<String, Object> params = effect.getParams();
                if (params != null && params.containsKey(paramName)) {
                    Object value = params.get(paramName);

                    if (clazz == Float.class && value instanceof Double) {
                        return clazz.cast(((Double) value).floatValue());
                    }
                    if (clazz == Integer.class && value instanceof Number) {
                        return clazz.cast(((Number) value).intValue());
                    }
                    if (clazz.isInstance(value)) {
                        return clazz.cast(value);
                    }
                    if (clazz == Color.class && value instanceof  String) {
                        return (T) hexToColor(String.valueOf(value));
                    }


                    AnimationFactory.logger.log(System.Logger.Level.ERROR, "Type mismatch for parameter: " + paramName + " (expected: " + clazz.getSimpleName() + ", found: " + value.getClass().getSimpleName() + ")");
                    return null;
                }
            }
        }

        AnimationFactory.logger.log(System.Logger.Level.WARNING, "Effect type or parameter not found: " + effectType + ", " + paramName);
        return null;
    }

    private Color hexToColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            throw new IllegalArgumentException("Invalid HEX string");
        }

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6 && hex.length() != 8) {
            throw new IllegalArgumentException("HEX string must be 6 or 8 characters long.");
        }

        long colorLong = Long.parseLong(hex, 16);

        if (hex.length() == 8) {
            int alpha = (int) (colorLong >> 24 & 0xFF);
            int red = (int) (colorLong >> 16 & 0xFF);
            int green = (int) (colorLong >> 8 & 0xFF);
            int blue = (int) (colorLong & 0xFF);
            return new Color(red, green, blue, alpha);
        } else {
            int red = (int) (colorLong >> 16 & 0xFF);
            int green = (int) (colorLong >> 8 & 0xFF);
            int blue = (int) (colorLong & 0xFF);
            return new Color(red, green, blue);
        }
    }

}
