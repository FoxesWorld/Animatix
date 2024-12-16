package org.foxesworld.animatix.animation.effect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.effects.ImageEffects;
import org.foxesworld.animatix.animation.imageEffect.effects.borderHighlight.BorderHighlightFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.bounce.BounceFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.colorFade.ColorFadeFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.crackFrame.CrackFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.decay.DecayFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.fade.FadeFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.move.MoveFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.rotate.RotateFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.spin.SpinFrame;
import org.foxesworld.animatix.animation.textEffect.effects.BounceTextAnimationFrame;
import org.foxesworld.animatix.animation.textEffect.effects.FadeTextAnimationFrame;
import org.foxesworld.animatix.animation.textEffect.effects.SlideTextAnimationFrame;
import org.foxesworld.animatix.animation.textEffect.effects.TextColorChangeFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationEffectFactory {

    private AnimationFactory animationFactory;
    public AnimationEffectFactory(){
    }

    public List<AnimationFrame> createImageEffects(AnimationPhase phase, JLabel label) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (String effectType : phase.getEffects()) {
            ImageEffects effect = ImageEffects.fromString(effectType);

            switch (effect) {
                case RESIZE -> frames.add(new ResizeFrame(animationFactory, phase, label));
                case MOVE -> frames.add(new MoveFrame(animationFactory, phase, label));
                case ROTATE -> frames.add(new RotateFrame(animationFactory, phase, label));
                case BOUNCE -> frames.add(new BounceFrame(animationFactory, phase, label));
                case DECAY -> frames.add(new DecayFrame(animationFactory, phase, label));
                case CRACK -> frames.add(new CrackFrame(animationFactory, phase, label));
                case FADE -> frames.add(new FadeFrame(animationFactory, phase, label));
                case COLORFADE -> frames.add(new ColorFadeFrame(animationFactory, phase, label));
                case SPIN -> frames.add(new SpinFrame(animationFactory, phase, label));
                case BORDERHIGHLIGHT ->  frames.add(new BorderHighlightFrame(animationFactory, phase, label));
                default -> throw new UnsupportedOperationException("Effect type not supported: " + effectType);
            }
        }

        return frames;
    }

    public List<AnimationFrame> createTextEffects(AnimationPhase phase, JLabel label) {
        List<AnimationFrame> frames = new ArrayList<>();
        for (String effect : phase.getEffects()) {
            switch (effect.toLowerCase()) {
                case "fade" -> frames.add(new FadeTextAnimationFrame(this.animationFactory, phase, label));
                case "slide" -> frames.add(new SlideTextAnimationFrame(this.animationFactory, phase, label));
                case "bounce" -> frames.add(new BounceTextAnimationFrame(this.animationFactory, phase, label));
                case "colorchange" -> frames.add(new TextColorChangeFrame(this.animationFactory, phase, label));

                // Добавьте другие эффекты
            }
        }
        return frames;
    }



    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
