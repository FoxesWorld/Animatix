package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.animation.config.Phase;

public interface AnimationStatus {
    void onPhaseCompleted(Phase phase);
}
