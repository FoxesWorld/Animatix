package org.foxesworld.animatix.animation.imageEffect;

import org.apache.commons.math3.util.FastMath;
import org.foxesworld.Main;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
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
        Main.LOGGER.info("Rotation effect applied in {} nanoseconds.", endTime - startTime);
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
            image.flush();
            image = null;
        }
        System.gc();
    }
}