package org.example.animation.imageEffect.effects;

import org.example.animation.AnimationFactory;
import org.example.animation.imageEffect.AnimationFrame;

import javax.swing.*;

public class MoveFrame extends AnimationFrame {

    public MoveFrame(AnimationFactory animationFactory) {
        super(animationFactory);
    }

    @Override
    public void update(float progress) {
        int newX = (int) (phase.getStartX() + progress * (phase.getEndX() - phase.getStartX()));
        int newY = (int) (phase.getStartY() + progress * (phase.getEndY() - phase.getStartY()));

        label.setLocation(newX, newY);
    }
}
