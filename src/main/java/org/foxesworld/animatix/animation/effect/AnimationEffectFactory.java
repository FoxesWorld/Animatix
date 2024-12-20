package org.foxesworld.animatix.animation.effect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.attributes.Phase;
import org.foxesworld.animatix.animation.config.attributes.Effect;
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

    public List<AnimationFrame> createImageEffects(Phase phase, JLabel label) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (Effect effect : phase.getEffects()) {
            ImageEffects imageEffect = ImageEffects.fromString(effect.getType());

            switch (imageEffect) {
                case RESIZE -> frames.add(new ResizeFrame(animationFactory, phase, label));
                case MOVE -> frames.add(new MoveFrame(animationFactory, phase, label));
                //case ROTATE -> frames.add(new RotateFrame(animationFactory, phase, label));
                case BOUNCE -> frames.add(new BounceFrame(animationFactory, phase, label));
                case DECAY -> frames.add(new DecayFrame(animationFactory, phase, label));
                case CRACK -> frames.add(new CrackFrame(animationFactory, phase, label));
                case FADE -> frames.add(new FadeFrame(animationFactory, phase, label));
                case COLORFADE -> frames.add(new ColorFadeFrame(animationFactory, phase, label));
                case SPIN -> frames.add(new SpinFrame(animationFactory, phase, label));
                case BORDERHIGHLIGHT -> frames.add(new BorderHighlightFrame(animationFactory, phase, label));
                default -> throw new UnsupportedOperationException("Effect type not supported: " + effect.getType());
            }
        }

        return frames;
    }

    public List<AnimationFrame> createTextEffects(Phase phase, JLabel label) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (Effect effect : phase.getEffects()) {
            switch (effect.getType().toLowerCase()) {
                case "fade" -> frames.add(new FadeTextAnimationFrame(this.animationFactory, phase, label));
                case "slide" -> frames.add(new SlideTextAnimationFrame(this.animationFactory, phase, label));
                case "bounce" -> frames.add(new BounceTextAnimationFrame(this.animationFactory, phase, label));
                case "colorchange" -> frames.add(new TextColorChangeFrame(this.animationFactory, phase, label));
                case "flip" -> frames.add(new FlipTextAnimationFrame(this.animationFactory, phase, label));
                case "fontchange" -> frames.add(new FontChangePixelAnimationFrame(this.animationFactory, phase, label));
                case "letterfade" -> frames.add(new FadeInTextAnimationFrame(this.animationFactory, phase, label));

                // Добавьте другие эффекты
                default -> throw new UnsupportedOperationException("Text effect type not supported: " + effect.getType());
            }
        }

        return frames;
    }
}
