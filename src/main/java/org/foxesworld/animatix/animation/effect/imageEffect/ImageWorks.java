package org.foxesworld.animatix.animation.effect.imageEffect;

import org.apache.commons.math3.util.FastMath;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.effect.imageEffect.effects.resize.ResizeFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

public class ImageWorks {

    public ImageWorks() {
    }


    public BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight, ResizeFrame.ResizeType resizeType) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        float originalAspect = (float) originalWidth / originalHeight;
        float targetAspect = (float) targetWidth / targetHeight;

        int newWidth = targetWidth;
        int newHeight = targetHeight;

        switch (resizeType) {
            case SCALE_TO_FIT -> {
                if (originalAspect > targetAspect) {
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / originalAspect);
                } else {
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * originalAspect);
                }
            }
            case SCALE_TO_COVER -> {
                if (originalAspect > targetAspect) {
                    newHeight = targetHeight;
                    newWidth = (int) (targetHeight * originalAspect);
                } else {
                    newWidth = targetWidth;
                    newHeight = (int) (targetWidth / originalAspect);
                }
            }
            case STRETCH -> {
                newWidth = targetWidth;
                newHeight = targetHeight;
            }
            default -> throw new IllegalArgumentException("Unsupported ResizeType: " + resizeType);
        }

        if (newWidth >= originalWidth && newHeight >= originalHeight) {
            return image;
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

    public BufferedImage applyRotationEffect(BufferedImage image, double startAngle, double endAngle, double speed, Runnable cleanupCallback) {
        long startTime = System.nanoTime();
        BufferedImage rotatedImage = null;

        try {
            ImageRotationWithCubicInterpolation rotation = new ImageRotationWithCubicInterpolation(image);
            double totalAngle = endAngle - startAngle;
            double elapsedTimeInSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
            double currentAngle = startAngle + (speed * elapsedTimeInSeconds * totalAngle);

            // Ограничим текущий угол значением endAngle, если скорость слишком велика.
            if ((speed > 0 && currentAngle > endAngle) || (speed < 0 && currentAngle < endAngle)) {
                currentAngle = endAngle;
            }

            rotatedImage = rotation.rotateImage(currentAngle);
        } finally {
            if (cleanupCallback != null) {
                cleanupCallback.run();
            }
        }

        long endTime = System.nanoTime();
        double totalElapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        AnimationFactory.logger.log(System.Logger.Level.INFO, "Rotation effect applied in {} seconds.", totalElapsedTimeInSeconds);

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

    public void dispose() {
        System.gc();
    }
}
