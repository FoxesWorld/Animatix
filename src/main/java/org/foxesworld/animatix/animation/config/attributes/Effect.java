package org.foxesworld.animatix.animation.config.attributes;

import java.util.Map;

public class Effect {
    private String type, font, textColor;
    private int fontSize;
    private long duration, delay;
    private Map<String, Object> params;

    public String getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public long getDelay() {
        return delay;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
