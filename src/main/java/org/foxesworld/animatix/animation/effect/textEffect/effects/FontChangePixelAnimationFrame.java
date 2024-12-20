package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.attributes.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.Map;

public class FontChangePixelAnimationFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startFont", "startFont", Font.class, new Font("Serif", Font.PLAIN, 12)),
            createParam("endFont", "endFont", Font.class, new Font("SansSerif", Font.BOLD, 24)),
            createParam("text", "text", String.class, "Animating Text")
    };

    private final String effectName = "fontchange";
    private String text;
    private Font startFont, endFont;

    public FontChangePixelAnimationFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
        label.setVisible(true);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        if (progress < 0.0f || progress > 1.0f) return;

        int interpolatedSize = (int) ((1 - progress) * startFont.getSize() + progress * endFont.getSize());
        int interpolatedStyle = progress < 0.5 ? startFont.getStyle() : endFont.getStyle();

        Font currentFont = new Font(startFont.getName(), interpolatedStyle, interpolatedSize);

        // Получаем компонент Graphics2D для рисования текста
        SwingUtilities.invokeLater(() -> {
            Graphics2D g2d = (Graphics2D) label.getGraphics();
            if (g2d != null) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(label.getForeground());

                // Создаём новый шрифт для рисования
                g2d.setFont(currentFont);

                // Рисуем текст по пикселям
                FontRenderContext frc = g2d.getFontRenderContext();
                TextLayout layout = new TextLayout(text, currentFont, frc);

                // Вычисляем позицию для центра текста
                int x = (label.getWidth() - (int) layout.getBounds().getWidth()) / 2;
                int y = (label.getHeight() + (int) layout.getBounds().getHeight()) / 2;

                layout.draw(g2d, x, y);
            }
            label.repaint();
        });
    }
}
