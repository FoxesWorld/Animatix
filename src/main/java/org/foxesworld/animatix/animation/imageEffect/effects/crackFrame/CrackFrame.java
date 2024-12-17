package org.foxesworld.animatix.animation.imageEffect.effects.crackFrame;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
<<<<<<< Updated upstream
=======
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.ImageAnimationFrame;
>>>>>>> Stashed changes

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CrackFrame extends ImageAnimationFrame {

    private List<Crack> cracks;
    private Random random;
    private int numCracks = 0;
    private final Map<String, Object>[] params = new Map[]{
            createParam("numCracks", "numCracks", Integer.class, 10)
    };
    private final String effectName = "crack";

    public CrackFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        initializeParams(params, effectName);
        cracks = new ArrayList<>();
        random = new Random();
    }

    @Override
    public void update(float progress) {
        for (Crack crack : cracks) {
            crack.update(progress);
        }
        drawCracks(progress);
    }

    @Override
    public void run() {
        super.run();
        generateCracks();
    }

    private void generateCracks() {
        //int numCracks = 5 + random.nextInt(5);
        for (int i = 0; i < numCracks; i++) {
            cracks.add(new Crack(random.nextInt(label.getWidth()), random.nextInt(label.getHeight())));
        }
    }

    private void drawCracks(float progress) {
        BufferedImage currentImage = imageWorks.getImage();
        if (currentImage == null) return;

        BufferedImage rgbImage = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        g.drawImage(currentImage, 0, 0, null);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));

        for (Crack crack : cracks) {
            crack.draw(g, progress);
        }

        g.dispose();

        for (int y = 0; y < currentImage.getHeight(); y++) {
            for (int x = 0; x < currentImage.getWidth(); x++) {
                int rgb = rgbImage.getRGB(x, y);
                int alpha = currentImage.getRGB(x, y) & 0xFF000000;
                int finalColor = alpha | (rgb & 0x00FFFFFF);
                currentImage.setRGB(x, y, finalColor);
            }
        }

        SwingUtilities.invokeLater(() -> label.setIcon(new ImageIcon(currentImage)));
    }

    private class Crack {
        private int startX, startY, endX, endY;
        private float length;
        private float maxLength = 100f;
        private int angle;

        public Crack(int startX, int startY) {
            this.startX = startX;
            this.startY = startY;
            this.angle = random.nextInt(360);
            this.length = 0;
            this.endX = startX + (int) (Math.cos(Math.toRadians(angle)) * maxLength);
            this.endY = startY + (int) (Math.sin(Math.toRadians(angle)) * maxLength);
        }

        public void update(float progress) {
            length = maxLength * progress;
        }

        public void draw(Graphics2D g, float progress) {
            int x2 = (int) (startX + Math.cos(Math.toRadians(angle)) * length);
            int y2 = (int) (startY + Math.sin(Math.toRadians(angle)) * length);
            g.drawLine(startX, startY, x2, y2);
        }
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

}
