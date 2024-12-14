package org.foxesworld.animatix.animation.area;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class JFrameWrapper implements AnimatableWindow {
    private final JFrame frame;

    public JFrameWrapper(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void addLabel(JLabel label) {
        frame.add(label);
    }

    @Override
    public int getWidth() {
        return frame.getWidth();
    }
}
