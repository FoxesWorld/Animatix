package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.effects.*;
import org.foxesworld.animatix.animation.imageEffect.effects.bounce.BounceFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.decay.DecayFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.move.MoveFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeFrame;
import org.foxesworld.animatix.animation.imageEffect.effects.rotate.RotateFrame;

import java.util.ArrayList;
import java.util.List;

public class AnimationEffectFactory {

    private AnimationFactory animationFactory;
    public AnimationEffectFactory(AnimationFactory animationFactory){
        this.animationFactory = animationFactory;
    }

    public List<AnimationFrame> createEffectsForPhase(AnimationPhase phase) {
        List<AnimationFrame> frames = new ArrayList<>();

        for (String effectType : phase.getImageEffects()) {
            Effects effect = Effects.fromString(effectType);

            switch (effect) {
                case RESIZE -> frames.add(new ResizeFrame(animationFactory));
                case MOVE -> frames.add(new MoveFrame(animationFactory));
                case ROTATE -> frames.add(new RotateFrame(animationFactory));
                case BOUNCE -> frames.add(new BounceFrame(animationFactory));
                case DECAY -> frames.add(new DecayFrame(animationFactory));
                default -> throw new UnsupportedOperationException("Effect type not supported: " + effectType);
            }
        }

        return frames;
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
