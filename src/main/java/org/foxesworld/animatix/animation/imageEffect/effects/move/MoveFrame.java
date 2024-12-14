package org.foxesworld.animatix.animation.imageEffect.effects.move;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import java.util.Map;

public class MoveFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startX", "startX", Integer.class, 0),
            createParam("startY", "startY", Integer.class, 0),
            createParam("endX", "endX", Integer.class, 100),
            createParam("endY", "endY", Integer.class, 100)
    };
    private final String effectName = "move";
    private int startX, endX, startY, endY;

    public MoveFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Вычисляем новые координаты
        int newX = (int) (startX + progress * (endX - startX));
        int newY = (int) (startY + progress * (endY - startY));

        // Устанавливаем новые координаты для JLabel
        label.setLocation(newX, newY);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }
}
