package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AnimationPhaseExecutor {

<<<<<<< Updated upstream
    private static final Logger logger = LoggerFactory.getLogger(AnimationPhaseExecutor.class);
    private AnimationFactory animationFactory;
=======
    private final AnimationFactory animationFactory;
>>>>>>> Stashed changes

    public AnimationPhaseExecutor(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    public void executePhase(AnimationPhase phase, List<AnimationFrame> animationFrames) {
        logger.info("Executing phase: {}", phase.getName());
        CountDownLatch latch = new CountDownLatch(animationFrames.size());

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

        try {
            latch.await();
            logger.info("Phase {} completed", phase.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Phase execution interrupted: {}", phase.getName());
        }

        notifyPhaseCompleted();
    }

<<<<<<< Updated upstream
    private void notifyPhaseCompleted() {
        logger.debug("Notifying factory about phase completion");
        animationFactory.onPhaseCompleted();
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
=======
    /**
     * Проверяет, что фабрика анимации корректно задана.
     *
     * @param animationFactory Экземпляр фабрики.
     */
    private void validateAnimationFactory(AnimationFactory animationFactory) {
        if (animationFactory == null) {
            throw new IllegalStateException("AnimationFactory is not set. Ensure it is properly initialized.");
        }
    }

    /**
     * Подготавливает список задач для выполнения.
     *
     * @param animationFrames Список кадров анимации.
     * @return Список задач в формате Callable.
     */
    private List<Callable<Void>> prepareTasks(List<AnimationFrame> animationFrames) {
        return animationFrames.stream()
                .<Callable<Void>>map(animationFrame -> () -> {
                    try {
                        animationFrame.run();
                        return null;
                    } catch (Exception e) {
                        AnimationFactory.logger.log(System.Logger.Level.ERROR,
                                "Error executing frame: {0}. Error: {1}",
                                animationFrame.getClass().getSimpleName(), e.getMessage(), e);
                        throw e;
                    }
                })
                .toList();
    }

    /**
     * Выполняет задачи через TaskExecutor с учетом времени выполнения.
     *
     * @param tasks         Список задач.
     * @param phaseDuration Длительность этапа.
     * @param phaseNum      Номер текущего этапа.
     */
    private void executeTasksWithTimeout(List<Callable<Void>> tasks, long phaseDuration, int phaseNum) throws Exception {
        animationFactory.getTaskExecutor().executeTasksWithTimeout(
                tasks,
                phaseDuration,
                exception -> AnimationFactory.logger.log(System.Logger.Level.ERROR,
                        "Task failed during phase {0}. Error: {1}",
                        phaseNum, exception.getMessage())
        );
    }

    /**
     * Уведомляет фабрику о завершении текущего этапа.
     *
     * @param phaseNum Номер текущего этапа.
     */
    private void notifyPhaseCompleted(AnimationPhase phase, int phaseNum) {
        AnimationFactory.logger.log(System.Logger.Level.INFO,
                "Notifying factory about phase {0} completion.", phaseNum);
        animationFactory.onPhaseCompleted(phase);
>>>>>>> Stashed changes
    }
}
