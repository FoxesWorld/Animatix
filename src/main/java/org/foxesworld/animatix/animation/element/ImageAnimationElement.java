package org.foxesworld.animatix.animation.element;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageAnimationElement extends BaseAnimationElement {
    private BufferedImage image;

    public ImageAnimationElement(String name, Rectangle bounds, boolean visible, BufferedImage image) {
        super(name, bounds, visible);
        this.image = image;
    }

    @Override
    public JComponent createComponent() {
        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(bounds);
        label.setVisible(visible);
        return label;
    }
}
