package org.foxesworld.animatix.animation.imageEffect;

import org.apache.commons.math3.util.FastMath;
import org.foxesworld.animatix.Main;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

public class ImageWorks {

    private BufferedImage image;
    private JLabel label;

    public ImageWorks(JLabel label) {
        this.label = label;
        this.image = (BufferedImage) ((ImageIcon) label.getIcon()).getImage();

    }

    public static ImageWorks createFromLabel(JLabel label) {
        BufferedImage img = label.getIcon() instanceof ImageIcon
                ? (BufferedImage) ((ImageIcon) label.getIcon()).getImage()
                : null;

        ImageWorks imageWorks = new ImageWorks(label);
        imageWorks.setImage(img);
        return imageWorks;
    }

    public static ImageWorks createFromImage(BufferedImage image) {
        JLabel label = new JLabel(new ImageIcon(image));
        ImageWorks imageWorks = new ImageWorks(label);
        imageWorks.setImage(image);
        return imageWorks;
    }


    public BufferedImage resizeImage(int targetWidth, int targetHeight, ResizeFrame.ResizeType resizeType) {
        // Получаем исходные размеры изображения
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Рассчитываем исходные и целевые соотношения сторон
        float originalAspect = (float) originalWidth / originalHeight;
        float targetAspect = (float) targetWidth / targetHeight;

        // Определяем размеры для нового изображения с учетом типа ресайза
        int newWidth = targetWidth;
        int newHeight = targetHeight;

        switch (resizeType) {
            case SCALE_TO_FIT -> {
                // Масштабируем так, чтобы изображение полностью помещалось в целевые размеры (не выходило за границы)
                if (originalAspect > targetAspect) {
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / originalAspect);
                } else {
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * originalAspect);
                }
            }
            case SCALE_TO_COVER -> {
                // Масштабируем так, чтобы изображение полностью покрывало целевую область (могут быть обрезки)
                if (originalAspect > targetAspect) {
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * originalAspect);
                } else {
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / originalAspect);
                }
            }
            case STRETCH -> {
                // Просто растягиваем изображение по целевым размерам
                newWidth = targetWidth;
                newHeight = targetHeight;
            }
            default -> throw new IllegalArgumentException("Unsupported ResizeType: " + resizeType);
        }

        // Если новое изображение меньше исходного по размеру, не изменяем его (используем оригинал)
        if (newWidth >= originalWidth && newHeight >= originalHeight) {
            // В случае уменьшения изображения, возвращаем оригинал, чтобы избежать размытия
            return image;
        }

        // Создаем новое изображение для ресайза
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        // Устанавливаем более качественные параметры интерполяции для уменьшения или растягивания изображения
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

    public BufferedImage applyRotationEffect(double angle, Runnable cleanupCallback) {
        long startTime = System.nanoTime();
        BufferedImage rotatedImage = null;

        try {
            ImageRotationWithCubicInterpolation rotation = new ImageRotationWithCubicInterpolation(image);
            rotatedImage = rotation.rotateImage(angle);
        } finally {
            if (cleanupCallback != null) {
                cleanupCallback.run();
            }
        }

        long endTime = System.nanoTime();
        double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        AnimationFactory.logger.log(System.Logger.Level.INFO, "Rotation effect applied in {} seconds.", elapsedTimeInSeconds);
        return rotatedImage;
    }

    public BufferedImage applyPixelDecayEffect(BufferedImage image, float alpha, int pixelDecaySpeed) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (FastMath.random() < alpha) {
                    int pixel = image.getRGB(x, y);
                    int a = (pixel >> 24) & 0xFF;
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;
                    a = Math.max(0, a - pixelDecaySpeed);
                    int newPixel = (a << 24) | (r << 16) | (g << 8) | b;
                    result.setRGB(x, y, newPixel);
                } else {
                    result.setRGB(x, y, 0);
                }
            }
        }
        return result;
    }

    public static BufferedImage getImageFromStream(String path) {
        if (path == null) return null;
        try (InputStream imageStream = AnimationFactory.class.getClassLoader().getResourceAsStream(path)) {
            return ImageIO.read(Objects.requireNonNull(imageStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load image from InputStream", e);
        }
    }

    public BufferedImage applyAlphaEffect(BufferedImage image, float alpha, int fadeSpeed) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int argb = image.getRGB(x, y);
                int originalAlpha = (argb >> 24) & 0xFF;
                int newAlpha = (int) (originalAlpha * alpha);
                newAlpha = Math.min(255, Math.max(0, newAlpha));

                if (fadeSpeed > 1) {
                    newAlpha = Math.min(255, newAlpha + fadeSpeed);
                }

                newImage.setRGB(x, y, (argb & 0x00FFFFFF) | (newAlpha << 24));
            }
        }
        return newImage;
    }


    public static BufferedImage setBaseAlpha(BufferedImage image, float alpha) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int argb = image.getRGB(x, y);
                int originalAlpha = (argb >> 24) & 0xFF;

                int newAlpha = (int) (originalAlpha * alpha);
                newAlpha = Math.min(255, Math.max(0, newAlpha));
                newImage.setRGB(x, y, (argb & 0x00FFFFFF) | (newAlpha << 24));
            }
        }

        return newImage;
    }



    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Rectangle getImageBounds() {
        return image.getAlphaRaster().getBounds();
    }

    public void dispose() {
        if (image != null) {
            image = null;
        }
        System.gc();
    }

    public JLabel getLabel() {
        return label;
    }
}
