package org.foxesworld.animatix.animation.area;

import javax.swing.JLabel;

public class KWindowWrapper implements AnimatableWindow {
    private final KWindow window;

    public KWindowWrapper(KWindow window) {
        this.window = window;
    }

    @Override
    public void addLabel(JLabel label) {
        window.add(label); // Assuming KWindow has an add method similar to JFrame
    }

    @Override
    public int getWidth() {
        return window.getWidth(); // Assuming KWindow has a getWidth method similar to JFrame
    }
}
