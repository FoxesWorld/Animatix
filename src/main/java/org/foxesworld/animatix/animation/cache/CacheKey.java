package org.foxesworld.animatix.animation.cache;

import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import java.util.Objects;

public class CacheKey {
    private final String animType;
    private final int phaseNum;
    private final String animName;
    private final float alpha;
    private final long delay;

    public CacheKey(AnimationConfig.AnimConf animConf, AnimationPhase phase) {
        this.animType = animConf.getType();
        this.animName = animConf.getName();
        this.phaseNum = phase.getPhaseNum();
        this.alpha = (float) phase.getAlpha();
        this.delay = phase.getDelay();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return phaseNum == cacheKey.phaseNum &&
                Float.compare(cacheKey.alpha, alpha) == 0 &&
                delay == cacheKey.delay &&
                animType.equals(cacheKey.animType) &&
                animName.equals(cacheKey.animName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(animType, phaseNum, animName, alpha, delay);
    }
}