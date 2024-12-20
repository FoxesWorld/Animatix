package org.foxesworld.animatix.animation.effect.imageEffect.effects.bounce;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.attributes.Phase;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

import javax.swing.*;

public class BounceFrame extends ImageAnimationFrame {
    public BounceFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
    }

    @Override
    public void update(float progress) {

    }
/*
    private boolean bounce;
    private double startX, endX, startY, endY;
    private double startWidth, endWidth, startHeight, endHeight;
    private double duration;

    public BounceFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);

        this.bounce = phase.isBounce();
        this.startX = phase.getStartX();
        this.endX = phase.getEndX();
        this.startY = phase.getStartY();
        this.endY = phase.getEndY();
        this.startWidth = phase.getStartWidth();
        this.endWidth = phase.getEndWidth();
        this.startHeight = phase.getStartHeight();
        this.endHeight = phase.getEndHeight();
        this.duration = phase.getDuration();
    }

    @Override
    public void update(float progress) {
        AnimationFactory.logger.info(progress);
        double newX = imageWorks.applyBounceEffect(startX, endX, progress * duration, duration, bounce);
        double newY = imageWorks.applyBounceEffect(startY, endY, progress * duration, duration, bounce);

        // Обновляем положение
        label.setLocation((int) newX, (int) newY);

        // Применяем эффект отскока для размера
        double newWidth = imageWorks.applyBounceEffect(startWidth, endWidth, progress * duration, duration, bounce);
        double newHeight = imageWorks.applyBounceEffect(startHeight, endHeight, progress * duration, duration, bounce);

        // Обновляем размер
        BufferedImage resizedImage = imageWorks.resizeImage((int) newWidth, (int) newHeight, phase.getResizeType());
        label.setSize((int) newWidth, (int) newHeight);
        label.setIcon(new ImageIcon(resizedImage));
        imageWorks.setImage(resizedImage);
    }
    */
}
