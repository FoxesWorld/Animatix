package org.example.animation;

import org.example.animation.config.AnimationPhase;
import org.example.animation.imageEffect.AnimationFrame;
import org.example.animation.imageEffect.effects.Effects;
import org.example.animation.imageEffect.effects.*;

import java.util.ArrayList;
import java.util.List;

public class AnimationEffectFactory {

    private final  AnimationFactory animationFactory;
    public AnimationEffectFactory(AnimationFactory animationFactory){
        this.animationFactory = animationFactory;
    }

    /**
     * Метод для создания эффектов для заданной фазы анимации
     *
     * @param phase фаза анимации, на основе которой создаются эффекты
     * @return список объектов AnimationFrame
     */
    public List<AnimationFrame> createEffectsForPhase(AnimationPhase phase) {
        List<AnimationFrame> frames = new ArrayList<>();

        // Добавляем эффекты в зависимости от типа фазы
        for (String effectType : phase.getTypes()) {
            // Преобразуем строковый тип эффекта в соответствующий тип enum
            Effects effect = Effects.fromString(effectType);

            switch (effect) {
                case RESIZE ->
                    // Передаем параметры фазы в конструктора эффекта ResizeFrame
                        frames.add(new ResizeFrame(animationFactory));
                case MOVE -> frames.add(new MoveFrame(animationFactory));
                case ROTATE -> frames.add(new RotateFrame(animationFactory));
                case BOUNCE -> frames.add(new BounceFrame(animationFactory));
                case DECAY -> frames.add(new DecayFrame(animationFactory));
                default -> throw new UnsupportedOperationException("Effect type not supported: " + effectType);
            }
        }

        return frames;
    }
}
