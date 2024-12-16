package org.foxesworld.animatix.animation.config;

import org.foxesworld.animatix.AnimationFactory;

import java.util.List;
import java.util.Map;

public class AnimationPhase {
    private int phaseNum;
    private double alpha;
    private List<String> effects;
    private String type, text, font, textColor, imagePath;
    private int fontSize;
    private List<String> textEffects;
    private long duration, delay;
    private Map<String, Map<String, Object>> effectParams;

    public String getType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getText() {
        return text;
    }

    public String getFont() {
        return font;
    }

    public String getTextColor() {
        return textColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public List<String> getTextEffects() {
        return textEffects;
    }

    public void setTextEffects(List<String> textEffects) {
        this.textEffects = textEffects;
    }

    public int getPhaseNum() {
        return phaseNum;
    }

    public void setPhaseNum(int phaseNum) {
        this.phaseNum = phaseNum;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Map<String, Map<String, Object>> getEffectParams() {
        return effectParams;
    }

    public void setEffectParams(Map<String, Map<String, Object>> effectParams) {
        this.effectParams = effectParams;
    }

    public List<String> getEffects() {
        return effects;
    }

    public void setEffects(List<String> effects) {
        this.effects = effects;
    }

    /**
     * Возвращает параметр для конкретного эффекта.
     *
     * @param effectName Название эффекта (например, "resize").
     * @param paramName  Название параметра (например, "startWidth").
     * @param clazz      Тип параметра.
     * @param <T>        Обобщенный тип.
     * @return Значение параметра или null, если оно отсутствует.
     */
    public <T> T getEffectParam(String effectName, String paramName, Class<T> clazz) {
        if (effectParams == null || !effectParams.containsKey(effectName)) {
            AnimationFactory.logger.log(System.Logger.Level.WARNING, "Effect name not found: " + effectName);
            return null;
        }

        Map<String, Object> effectMap = effectParams.get(effectName);
        if (!effectMap.containsKey(paramName)) {
            AnimationFactory.logger.log(System.Logger.Level.WARNING, "Parameter name not found: " + paramName + " for effect: " + effectName);
            return null;
        }

        Object value = effectMap.get(paramName);

        // Проверяем, если нужно конвертировать типы
        if (clazz == Float.class && value instanceof Double) {
            return clazz.cast(((Double) value).floatValue());
        }
        if (clazz == Integer.class && value instanceof Number) {
            return clazz.cast(((Number) value).intValue());
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        AnimationFactory.logger.log(System.Logger.Level.ERROR, "Type mismatch for parameter: " + paramName + " (expected: " + clazz.getSimpleName() + ", found: " + value.getClass().getSimpleName() + ")");
        return null;
    }

    /**
     * Устанавливает параметры для конкретного эффекта из карты параметров.
     *
     * @param effectName Название эффекта.
     * @param params     Карта параметров.
     */
    public void setEffectParams(String effectName, Map<String, Object> params) {
        if (effectParams != null) {
            effectParams.put(effectName, params);
        }
    }

    /**
     * Проверяет, используется ли указанный эффект в текущей фазе.
     *
     * @param effectName Название эффекта.
     * @return true, если эффект используется, иначе false.
     */
    public boolean hasEffect(String effectName) {
        return (effects != null && effects.contains(effectName)) ||
                (textEffects != null && textEffects.contains(effectName));
    }

    public double getAlpha() {
        return alpha;
    }
}
