package org.foxesworld.animatix.animation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimatedPanel extends JPanel {

    private final List<JLabel> animationElements = new ArrayList<>();

    // Добавляем анимационные элементы
    public void addAnimationElement(JLabel element) {
        animationElements.add(element);
        this.add(element);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Рисуем все элементы на панели
        for (JLabel element : animationElements) {
            if (element.isVisible()) {
                element.paint(g); // Рисуем каждый элемент
            }
        }
    }
}
