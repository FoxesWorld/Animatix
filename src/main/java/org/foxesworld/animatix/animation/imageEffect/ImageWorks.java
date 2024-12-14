package org.foxesworld.animatix.animation.imageEffect;

import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.util.FastMath;
import org.foxesworld.Main;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

public class ImageWorks {

    private BufferedImage image;

    public ImageWorks(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage resizeImage(int targetWidth, int targetHeight, ResizeFrame.ResizeType resizeType) {
        int newWidth = targetWidth;
        int newHeight = targetHeight;

        float originalAspect = (float) image.getWidth() / image.getHeight();
        float targetAspect = (float) targetWidth / targetHeight;

        switch (resizeType) {
            case SCALE_TO_FIT:
                // Для SCALE_TO_FIT изображение будет уменьшаться, чтобы вписаться в targetWidth и targetHeight
                if (originalAspect > targetAspect) {
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / originalAspect);
                } else {
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * originalAspect);
                }
                break;

            case SCALE_TO_COVER:
                // Для SCALE_TO_COVER изображение будет масштабироваться так, чтобы оно полностью покрывало целевую область
                if (originalAspect > targetAspect) {
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * originalAspect);
                } else {
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / originalAspect);
                }
                break;

            case STRETCH:
                // Для STRETCH изображение будет растягиваться на весь целевой размер
                newWidth = targetWidth;
                newHeight = targetHeight;
                break;

            default:
                throw new IllegalArgumentException("Unsupported ResizeType: " + resizeType);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }

    public double applyBounceEffect(double startValue, double endValue, double elapsedTime, double duration, boolean bounce) {
        double progress = elapsedTime / duration;

        if (bounce) {
            progress -= FastMath.sin(2 * FastMath.PI * progress) * 0.3;
        }

        return startValue + (endValue - startValue) * Math.min(1.0, Math.max(0.0, progress));
    }

    public BufferedImage applyRotationEffect(double angle) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Проверка на корректность размеров
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions: width and height must be greater than zero");
        }

        // Преобразование угла в радианы с использованием точности Math3
        double radians = FastMath.toRadians(angle);
        double sin = FastMath.abs(FastMath.sin(radians));
        double cos = FastMath.abs(FastMath.cos(radians));

        // Вычисление новых размеров с учётом угла поворота
        int newWidth = (int) FastMath.ceil(width * FastMath.abs(cos) + height * FastMath.abs(sin));
        int newHeight = (int) FastMath.ceil(height * FastMath.abs(cos) + width * FastMath.abs(sin));

        // Проверка на выход за пределы
        if (newWidth <= 0 || newHeight <= 0) {
            return image;  // Возвращаем оригинальное изображение, если вычисленные размеры некорректны
        }

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotatedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Применяем трансформацию поворота
        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        transform.rotate(radians, width / 2.0, height / 2.0);

        try {
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            g.drawImage(op.filter(image, null), 0, 0, null);
        } catch (Exception e) {
            Main.LOGGER.error("Error during rotation: " + e.getMessage());
        } finally {
            g.dispose();
            dispose();
        }

        return rotatedImage;
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage applyPixelDecayEffect(BufferedImage image, float alpha, int pixelDecaySpeed) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Random pixel removement
                if (FastMath.random() < alpha) {
                    int pixel = image.getRGB(x, y);
                    int a = (pixel >> 24) & 0xFF; // Alpha
                    int r = (pixel >> 16) & 0xFF; // Red
                    int gValue = (pixel >> 8) & 0xFF; // Green
                    int b = pixel & 0xFF; // Blue
                    a = Math.max(0, a - pixelDecaySpeed);

                    int newPixel = (a << 24) | (r << 16) | (gValue << 8) | b;
                    result.setRGB(x, y, newPixel);
                } else {
                    result.setRGB(x, y, 0); // full transparent
                }
            }
        }

        g.dispose();
        return result;
    }

    public static BufferedImage getImageFromStream(String path) {
        BufferedImage image = null;
        if (path != null) {
            try (InputStream imageStream = AnimationFactory.class.getClassLoader().getResourceAsStream(path)) {
                image = ImageIO.read(Objects.requireNonNull(imageStream));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load image from InputStream", e);
            }
        }
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Rectangle getImageBounds() {
        return this.image.getAlphaRaster().getBounds();
    }

    public void dispose() {
        // Очистка графических объектов, если они использовались
        if (image != null) {
            image.flush();
            image = null;
        }

        System.gc();  // Принудительная сборка мусора, если необходимо
    }
}
