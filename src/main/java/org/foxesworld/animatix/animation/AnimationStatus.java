package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.animation.config.attributes.Phase;

public interface AnimationStatus {
    void onPhaseCompleted(Phase phase);
}
