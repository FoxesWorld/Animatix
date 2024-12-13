package org.foxesworld.animatix.animation.imageEffect;

import org.apache.commons.math3.util.FastMath;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.imageEffect.effects.resize.ResizeType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageWorks {

    private BufferedImage image;

    public ImageWorks(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage resizeImage(int targetWidth, int targetHeight, ResizeType resizeType) {
        int newWidth = targetWidth;
        int newHeight = targetHeight;

        if (resizeType == ResizeType.SCALE_TO_FIT || resizeType == ResizeType.SCALE_TO_COVER) {
            float originalAspect = (float) image.getWidth() / image.getHeight();
            float targetAspect = (float) targetWidth / targetHeight;

            if ((resizeType == ResizeType.SCALE_TO_FIT && originalAspect > targetAspect) ||
                    (resizeType == ResizeType.SCALE_TO_COVER && originalAspect < targetAspect)) {
                newWidth = (int) (targetHeight * originalAspect);
                newHeight = targetHeight;
            } else {
                newWidth = targetWidth;
                newHeight = (int) (targetWidth / originalAspect);
            }
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

        double radians = FastMath.toRadians(angle);
        double sin = FastMath.abs(FastMath.sin(radians));
        double cos = FastMath.abs(FastMath.cos(radians));

        // Вычисление новых размеров изображения с учетом угла
        int newWidth = (int) FastMath.floor(width * cos + height * sin);
        int newHeight = (int) FastMath.floor(height * cos + width * sin);

        // Создаем трансформацию
        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        transform.rotate(radians, width / 2.0, height / 2.0);

        // Создание нового изображения с учетом измененных размеров
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // Получаем Graphics2D и рисуем изображение
        Graphics2D g = rotatedImage.createGraphics();
        try {
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            // Применяем трансформацию и рисуем изображение
            g.drawImage(op.filter(image, null), 0, 0, null);
        } finally {
            // Обязательно вызываем dispose() для освобождения ресурсов
            g.dispose();
        }

        // Возвращаем повернутое изображение
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
                if (Math.random() > alpha) {
                    result.setRGB(x, y, 0);
                } else {
                    result.setRGB(x, y, image.getRGB(x, y));
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
                image = ImageIO.read(imageStream);
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
}
