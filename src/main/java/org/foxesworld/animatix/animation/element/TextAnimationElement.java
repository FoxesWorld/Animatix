package org.foxesworld.animatix.animation.element;

import javax.swing.*;
import java.awt.*;

public class TextAnimationElement extends BaseAnimationElement {
    private String text;
    private Font font;
    private Color textColor;

    public TextAnimationElement(String name, Rectangle bounds, boolean visible, String text, Font font, Color textColor) {
        super(name, bounds, visible);
        this.text = text;
        this.font = font;
        this.textColor = textColor;
    }

    @Override
    public JComponent createComponent() {
        JLabel label = new JLabel(text);
        label.setBounds(bounds);
        label.setFont(font);
        label.setForeground(textColor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVisible(visible);
        return label;
    }
}
