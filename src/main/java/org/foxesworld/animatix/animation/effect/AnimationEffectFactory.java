package org.foxesworld.animatix.animation.effect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.config.Effect;
import org.foxesworld.animatix.animation.config.KeyFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.ImageEffects;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.borderHighlight.BorderHighlightFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.bounce.BounceFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.colorFade.ColorFadeFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.crackFrame.CrackFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.decay.DecayFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.fade.FadeFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.move.MoveFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.resize.ResizeFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.spin.SpinFrame;
import org.foxesworld.animatix.animation.effect.textEffect.effects.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationEffectFactory {

    private final AnimationFactory animationFactory;

    public AnimationEffectFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    public List<AnimationFrame> createImageEffects(AnimationPhase phase, JLabel label, KeyFrame keyFrame) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (Effect effect : keyFrame.getEffects()) {
            ImageEffects imageEffect = ImageEffects.fromString(effect.getType());

            switch (imageEffect) {
                case RESIZE -> frames.add(new ResizeFrame(animationFactory, keyFrame, phase, label));
                case MOVE -> frames.add(new MoveFrame(animationFactory, keyFrame,phase, label));
                //case ROTATE -> frames.add(new RotateFrame(animationFactory, phase, label));
                case BOUNCE -> frames.add(new BounceFrame(animationFactory, keyFrame, phase, label));
                case DECAY -> frames.add(new DecayFrame(animationFactory, keyFrame, phase, label));
                case CRACK -> frames.add(new CrackFrame(animationFactory, keyFrame, phase, label));
                case FADE -> frames.add(new FadeFrame(animationFactory, keyFrame, phase, label));
                case COLORFADE -> frames.add(new ColorFadeFrame(animationFactory, keyFrame, phase, label));
                case SPIN -> frames.add(new SpinFrame(animationFactory, keyFrame, phase, label));
                case BORDERHIGHLIGHT -> frames.add(new BorderHighlightFrame(animationFactory, keyFrame, phase, label));
                default -> throw new UnsupportedOperationException("Effect type not supported: " + effect.getType());
            }
        }

        return frames;
    }

    public List<AnimationFrame> createTextEffects(AnimationPhase phase, JLabel label, KeyFrame keyFrame) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (Effect effect : keyFrame.getEffects()) {
            switch (effect.getType().toLowerCase()) {
                case "fade" -> frames.add(new FadeTextAnimationFrame(this.animationFactory, keyFrame, phase, label));
                case "slide" -> frames.add(new SlideTextAnimationFrame(this.animationFactory, keyFrame, phase, label));
                case "bounce" -> frames.add(new BounceTextAnimationFrame(this.animationFactory, keyFrame, phase, label));
                case "colorchange" -> frames.add(new TextColorChangeFrame(this.animationFactory,  keyFrame,phase, label));
                case "flip" -> frames.add(new FlipTextAnimationFrame(this.animationFactory, keyFrame, phase, label));
                case "fontchange" -> frames.add(new FontChangePixelAnimationFrame(this.animationFactory, keyFrame, phase, label));
                case "letterfade" -> frames.add(new FadeInTextAnimationFrame(this.animationFactory, keyFrame, phase, label));

                // Добавьте другие эффекты
                default -> throw new UnsupportedOperationException("Text effect type not supported: " + effect.getType());
            }
        }

        return frames;
    }
}
