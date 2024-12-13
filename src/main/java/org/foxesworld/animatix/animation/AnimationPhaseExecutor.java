package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AnimationPhaseExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AnimationPhaseExecutor.class);
    private AnimationFactory animationFactory;

    public AnimationPhaseExecutor(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    /**
     * Выполняет анимацию для указанной фазы.
     *
     * @param phase            текущая анимационная фаза
     * @param animationFrames  список фреймов анимации для выполнения
     */
    public void executePhase(AnimationPhase phase, List<AnimationFrame> animationFrames) {
        logger.info("Executing phase: {}", phase.getName());
        CountDownLatch latch = new CountDownLatch(animationFrames.size());

        // Запускаем все фреймы в отдельных потоках
        for (AnimationFrame animationFrame : animationFrames) {
            new Thread(() -> {
                try {
                    animationFrame.run();
                    logger.debug("Frame completed: {}", animationFrame.getClass().getSimpleName());
                } catch (Exception e) {
                    logger.error("Error executing frame: {}", animationFrame.getClass().getSimpleName(), e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Ждём завершения всех фреймов
        try {
            latch.await();
            logger.info("Phase {} completed", phase.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Phase execution interrupted: {}", phase.getName());
        }

        // Уведомляем фабрику о завершении фазы
        notifyPhaseCompleted();
    }

    /**
     * Уведомляет фабрику о завершении текущей фазы.
     */
    private void notifyPhaseCompleted() {
        logger.debug("Notifying factory about phase completion");
        animationFactory.onPhaseCompleted();
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
