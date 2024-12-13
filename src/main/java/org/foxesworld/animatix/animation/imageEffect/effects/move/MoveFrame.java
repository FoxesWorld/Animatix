package org.foxesworld.animatix.animation.imageEffect.effects.move;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

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
