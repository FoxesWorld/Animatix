package org.foxesworld.animatix.animation.cache;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private final Map<String, BufferedImage> imageCache = new HashMap<>();

    public ImageCache(){

    }

    public BufferedImage getCachedImage(String key) {
        return imageCache.get(key);
    }

    public void cacheImage(String key, BufferedImage image) {
        imageCache.put(key, image);
    }
}
