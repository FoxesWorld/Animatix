package org.foxesworld.animatix.animation.imageEffect.effects.borderHighlight;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Map;

public class BorderHighlightFrame extends AnimationFrame {
    // Параметры эффекта
    private final Map<String, Object>[] params = new Map[] {
            createParam("borderColor", "borderColor", String.class, "#FF5733"),  // HEX цвет для границы
            createParam("shadowColor", "shadowColor", String.class, "#000000"),  // HEX цвет для тени
            createParam("borderSize", "borderSize", Integer.class, 1),          // Размер границы
            createParam("shadowSize", "shadowSize", Integer.class, 3)            // Размер тени
    };

    private final String effectName = "borderHighlight";

    // Поля для хранения параметров
    private String borderColor, shadowColor;
    private int borderSize, shadowSize;
    private BufferedImage originalImage;

    public BorderHighlightFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        this.originalImage = animationFactory.getImageWorks().getImage();
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Применяем эффект на изображение
        BufferedImage currentImage = applyBorderHighlightEffect(originalImage, progress);

        // Обновляем изображение в анимации
        SwingUtilities.invokeLater(() -> {
            label.setIcon(new ImageIcon(currentImage));
        });
        this.getAnimationFactory().getImageWorks().setImage(currentImage);
    }

    private BufferedImage applyBorderHighlightEffect(BufferedImage image, float progress) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();

        // Применяем исходное изображение с учетом альфа-канала
        g2d.drawImage(image, 0, 0, null);

        // Создаем контур для непрозрачных пикселей
        GeneralPath shape = new GeneralPath();
        boolean isFirstPoint = true;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                if (pixelColor.getAlpha() > 0) { // Если пиксель непрозрачный
                    if (isFirstPoint) {
                        shape.moveTo(x, y); // Начинаем контур с первого пикселя
                        isFirstPoint = false;
                    } else {
                        shape.lineTo(x, y); // Добавляем пиксель в контур
                    }
                }
            }
        }

        // Применяем прогрессию для границы
        Color borderColor = hexToColor(this.borderColor, progress);
        Color borderEffectColor = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), borderColor.getAlpha());

        // Рисуем границу вокруг изображения с прогрессией
        g2d.setColor(borderEffectColor);
        g2d.setStroke(new BasicStroke(borderSize));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.draw(shape);

        // Применяем прогрессию для тени
        Color shadowColor = hexToColor(this.shadowColor, progress);
        Color shadowEffectColor = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), shadowColor.getAlpha());

        // Настроим прозрачность для тени
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(shadowEffectColor);
        g2d.setStroke(new BasicStroke(shadowSize));

        // Трансформируем контур для создания смещения тени
        AffineTransform transform = AffineTransform.getTranslateInstance(5, 5);
        g2d.draw(transform.createTransformedShape(shape));

        // Освобождаем ресурсы
        g2d.dispose();

        return newImage;
    }




    // Метод для конвертации HEX строки в Color с учетом альфа-канала
    private Color hexToColor(String hex, float progress) {
        Color color = Color.decode(hex);
        int alpha = (int) (255 * progress); // Применяем прогрессию альфа-канала
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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
