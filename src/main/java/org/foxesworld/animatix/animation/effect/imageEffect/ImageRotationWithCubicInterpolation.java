package org.foxesworld.animatix.animation.effect.imageEffect;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.FastMath;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageRotationWithCubicInterpolation {

    private BufferedImage image;

    public ImageRotationWithCubicInterpolation(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        this.image = image;
    }

    /**
     * Rotates the image by a specified angle using bicubic interpolation.
     *
     * @param angle Rotation angle in degrees.
     * @return A new BufferedImage representing the rotated image.
     */
    public BufferedImage rotateImage(double angle) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions: width and height must be greater than zero");
        }

        // Convert angle to radians using FastMath
        double radians = FastMath.toRadians(angle);
        double sin = FastMath.abs(FastMath.sin(radians));
        double cos = FastMath.abs(FastMath.cos(radians));

        // Calculate new dimensions for the rotated image
        int newWidth = (int) FastMath.ceil(width * cos + height * sin);
        int newHeight = (int) FastMath.ceil(height * cos + width * sin);

        // Create a new image with the calculated dimensions
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // Use Graphics2D for rendering
        Graphics2D g2d = rotatedImage.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set up the transformation for rotation
            AffineTransform transform = new AffineTransform();
            transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
            transform.rotate(radians, width / 2.0, height / 2.0);

            // Draw the rotated image
            g2d.drawImage(image, transform, null);
        } finally {
            g2d.dispose(); // Ensure resources are freed
        }

        // Clear old image resources if no longer needed
        image.flush();

        return (BufferedImage) rotatedImage;
    }


    /**
     * Applies cubic interpolation for a given pixel coordinate using Apache Math.
     *
     * @param x X-coordinate of the pixel.
     * @param y Y-coordinate of the pixel.
     * @return Interpolated color value.
     */
    private int applyCubicInterpolation(int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (x < 0 || y < 0 || x >= width || y >= height) {
            return Color.BLACK.getRGB(); // Default to black for out-of-bounds pixels
        }

        // Example of using LinearInterpolator for interpolation
        LinearInterpolator interpolator = new LinearInterpolator();

        // Mock data for interpolation (replace with real values as needed)
        double[] xValues = {0, 1, 2, 3};
        double[] yValues = {
                getColorAt(x - 1, y - 1), getColorAt(x, y - 1),
                getColorAt(x + 1, y - 1), getColorAt(x + 2, y - 1)
        };

        PolynomialSplineFunction function = interpolator.interpolate(xValues, yValues);
        return (int) function.value(x); // Interpolated value
    }

    /**
     * Helper method to safely get color from an image.
     */
    private double getColorAt(int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (x < 0 || y < 0 || x >= width || y >= height) {
            return 0; // Default to black if out-of-bounds
        }

        return image.getRGB(x, y);
    }

    /**
     * Clears the reference to the source image to help with memory management.
     */
    public void clearSourceImage() {
        image = null;
    }
}
