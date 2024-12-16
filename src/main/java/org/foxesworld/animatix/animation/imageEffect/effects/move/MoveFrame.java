package org.foxesworld.animatix.animation.imageEffect.effects.move;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveFrame extends AnimationFrame {
    private final Map<String, Object>[] params = new Map[] {
            createParam("startX", "startX", Integer.class, 0),
            createParam("startY", "startY", Integer.class, 0),
            createParam("endX", "endX", Integer.class, 100),
            createParam("endY", "endY", Integer.class, 100),
            createParam("traceColor", "traceColor", Color.class, Color.GRAY),
            createParam("traceThickness", "traceThickness", Integer.class, 2)
    };

    private final String effectName = "move";
    private int startX, endX, startY, endY;
    private List<Point> pathPoints;
    private Color traceColor;
    private int traceThickness;

    public MoveFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        pathPoints = new ArrayList<>();
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Вычисляем новые координаты
        int newX = (int) (startX + progress * (endX - startX));
        int newY = (int) (startY + progress * (endY - startY));

        // Добавляем текущую точку в путь
        pathPoints.add(new Point(newX, newY));

        // Устанавливаем новые координаты для JLabel
        label.setLocation(newX, newY);
        //drawTrace();
    }

    private void drawTrace() {
        BufferedImage currentImage = imageWorks.getImage();
        if (currentImage == null) return;

        // Создаем графику для рисования трейса
        Graphics2D g = currentImage.createGraphics();
        g.setColor(traceColor);
        g.setStroke(new BasicStroke(traceThickness));

        // Рисуем линии между точками в пути
        for (int i = 1; i < pathPoints.size(); i++) {
            Point p1 = pathPoints.get(i - 1);
            Point p2 = pathPoints.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        g.dispose();

        SwingUtilities.invokeLater(() -> {
            label.setIcon(new ImageIcon(currentImage));
            imageWorks.setImage(currentImage);
            label.repaint();
        });
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void run() {
        super.run();
    }
}
