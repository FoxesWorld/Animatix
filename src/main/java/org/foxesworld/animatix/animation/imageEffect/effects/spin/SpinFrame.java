package org.foxesworld.animatix.animation.imageEffect.effects.spin;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.ImageAnimationFrame;

public class SpinFrame extends ImageAnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startAngle", "startAngle", Double.class, 0.0),
            createParam("endAngle", "endAngle", Double.class, 360.0),
            createParam("spinSpeed", "spinSpeed", Integer.class, 5)
    };

    private final String effectName = "spin";

    private double startAngle, endAngle;
    private int spinSpeed;

    public SpinFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Вычисляем текущий угол поворота
        double currentAngle = startAngle + progress * (endAngle - startAngle);

        // Применяем вращение к изображению
        BufferedImage rotatedImage = rotateImage(imageWorks.getImage(), currentAngle);
        label.setIcon(new ImageIcon(rotatedImage));
        imageWorks.setImage(rotatedImage);
    }

    /**
     * Метод для вращения изображения на указанный угол.
     *
     * @param image зображение для вращения.
     * @param angle Угол поворота (в градусах).
     * @return Вращённое изображение.
     */
    private BufferedImage rotateImage(BufferedImage image, double angle) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Вычисляем центр изображения
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // Создаём AffineTransform для вращения
        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(Math.toRadians(angle));
        transform.translate(-centerX, -centerY);

        // Рисуем изображение с применением трансформации
        g2d.drawImage(image, transform, null);
        g2d.dispose();

        return rotatedImage;
    }
}
