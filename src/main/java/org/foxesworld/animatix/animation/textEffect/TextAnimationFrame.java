package org.foxesworld.animatix.animation.textEffect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;

public abstract class TextAnimationFrame extends AnimationFrame {


    protected final String text;
    protected final String font;
    protected final int fontSize;
    protected final String textColor;

    public TextAnimationFrame(AnimationFactory animationFactory, AnimationPhase animationPhase, JLabel label) {
        super(animationFactory, animationPhase, label);
        this.text = "text";
        this.font =  "Arial";
        this.fontSize = 16;
        this.textColor = "#000000";
    }

    /**
     * Метод для применения эффекта текста.
     * Он будет вызван на каждом шаге анимации.
     *
     * @param progress Прогресс анимации (от 0 до 1)
     */
    public abstract void applyEffect(float progress);

    @Override
    public void update(float progress) {
        // В этом методе просто вызываем абстрактный метод, который будет реализован в подклассах
        applyEffect(progress);
    }

    @Override
    public void run() {
        super.run();  // Запуск базового класса для анимации
    }
}