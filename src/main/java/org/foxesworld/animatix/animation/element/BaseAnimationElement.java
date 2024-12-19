package org.foxesworld.animatix.animation.element;

import javax.swing.*;
import java.awt.*;

public abstract class BaseAnimationElement {
    protected String name;
    protected Rectangle bounds;
    protected boolean visible;

    public BaseAnimationElement(String name, Rectangle bounds, boolean visible) {
        this.name = name;
        this.bounds = bounds;
        this.visible = visible;
    }

    public abstract JComponent createComponent();

    public String getName() {
        return name;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
