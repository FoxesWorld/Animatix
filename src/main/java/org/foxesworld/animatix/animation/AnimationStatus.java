package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.animation.config.AnimationPhase;

public interface AnimationStatus {
    void onPhaseCompleted(AnimationPhase phase);
}
