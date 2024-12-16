package org.foxesworld.animatix.animation.effect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.effects.*;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationEffectFactory {

    private AnimationFactory animationFactory;
    public AnimationEffectFactory(){
    }

    public List<AnimationFrame> createEffectsForPhase(AnimationPhase phase) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (String effectType : phase.getImageEffects()) {
            ImageEffects effect = ImageEffects.fromString(effectType);

            switch (effect) {
                case RESIZE -> frames.add(new ResizeFrame(animationFactory));
                case MOVE -> frames.add(new MoveFrame(animationFactory));
                case ROTATE -> frames.add(new RotateFrame(animationFactory));
                case BOUNCE -> frames.add(new BounceFrame(animationFactory));
                case DECAY -> frames.add(new DecayFrame(animationFactory));
                case CRACK -> frames.add(new CrackFrame(animationFactory));
                case FADE -> frames.add(new FadeFrame(animationFactory));
                case COLORFADE -> frames.add(new ColorFadeFrame(animationFactory));
                case SPIN -> frames.add(new SpinFrame(animationFactory));
                case BORDERHIGHLIGHT ->  frames.add(new BorderHighlightFrame(animationFactory));
                default -> throw new UnsupportedOperationException("Effect type not supported: " + effectType);
            }
        }

        return frames;
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
