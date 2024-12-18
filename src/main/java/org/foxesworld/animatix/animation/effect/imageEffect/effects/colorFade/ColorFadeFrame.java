package org.foxesworld.animatix.animation.effect.imageEffect.effects.colorFade;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ColorFadeFrame extends ImageAnimationFrame {
    private final Map<String, Object>[] params = new Map[]{
            createParam("startColor", "startColor", String.class, "#FFFFFF"),
            createParam("endColor", "endColor", String.class, "#000000")
    };

    private final String effectName = "colorFade";

    private String startColor, endColor;
    private Color currentColor;
    private float progress;

    public ColorFadeFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        this.progress = progress;

        Color startColor = hexToColor(this.startColor);
        Color endColor = hexToColor(this.endColor);
        int r = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
        int g = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
        int b = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);

        int alpha = startColor.getAlpha();
        currentColor = new Color(r, g, b, alpha);

        SwingUtilities.invokeLater(() -> {
            label.setForeground(currentColor);
        });

        if (image != null) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color pixelColor = new Color(image.getRGB(x, y), true);

                    int newR = (int) (pixelColor.getRed() + (currentColor.getRed() - pixelColor.getRed()) * progress);
                    int newG = (int) (pixelColor.getGreen() + (currentColor.getGreen() - pixelColor.getGreen()) * progress);
                    int newB = (int) (pixelColor.getBlue() + (currentColor.getBlue() - pixelColor.getBlue()) * progress);

                    image.setRGB(x, y, new Color(newR, newG, newB, pixelColor.getAlpha()).getRGB());
                }
            }

            SwingUtilities.invokeLater(() -> {
                label.setIcon(new ImageIcon(image));
                imageCache.cacheImage(label.getName(), image);
            });

        }
    }

    private Color hexToColor(String hex) {
        return Color.decode(hex);
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }
}
