package org.foxesworld.animatix.animation.effect.imageEffect.effects.move;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.attributes.Phase;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveFrame extends ImageAnimationFrame {
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

    public MoveFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
        pathPoints = new ArrayList<>();
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        label.setIcon(new ImageIcon(image));
        int newX = (int) (startX + progress * (endX - startX));
        int newY = (int) (startY + progress * (endY - startY));

        pathPoints.add(new Point(newX, newY));

        label.setLocation(newX, newY);
        //drawTrace();
    }

    private void drawTrace() {
        if (image == null) return;

            Graphics2D g = image.createGraphics();
            g.setColor(traceColor);
            g.setStroke(new BasicStroke(traceThickness));

            for (int i = 1; i < pathPoints.size(); i++) {
                Point p1 = pathPoints.get(i - 1);
                Point p2 = pathPoints.get(i);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            g.dispose();

            imageCache.cacheImage(label.getName(), image);

        BufferedImage finalCurrentImage = image;
        SwingUtilities.invokeLater(() -> {
            label.setIcon(new ImageIcon(finalCurrentImage));
            //imageWorks.setImage(finalCurrentImage);
            imageCache.cacheImage(label.getName(), finalCurrentImage);
            label.repaint();
        });
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }
}
