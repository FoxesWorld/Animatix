package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import java.util.List;
import java.util.concurrent.Callable;

public class AnimationPhaseExecutor {

    private AnimationFactory animationFactory;

    public AnimationPhaseExecutor() {
    }

    /**
     * Выполняет заданный этап анимации.
     *
     * @param animationFactory Фабрика анимаций.
     * @param animationFrames  Список кадров анимации для выполнения.
     * @param phaseNum         Номер текущего этапа.
     */
    public void executePhase(AnimationFactory animationFactory, List<AnimationFrame> animationFrames, int phaseNum) {
        validateAnimationFactory(animationFactory);
        long phaseDuration = animationFactory.getCurrentPhase().getDuration();

        AnimationFactory.logger.log(System.Logger.Level.INFO,
                "Starting execution of phase {0}. Duration: {1} ms", phaseNum, phaseDuration);

        List<Callable<Void>> tasks = prepareTasks(animationFrames);

        try {
            executeTasksWithTimeout(tasks, phaseDuration, phaseNum);
            AnimationFactory.logger.log(System.Logger.Level.INFO, "Phase {0} completed successfully.", phaseNum);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AnimationFactory.logger.log(System.Logger.Level.WARNING,
                    "Phase execution interrupted. Phase: {0}", phaseNum);
        } catch (Exception e) {
            AnimationFactory.logger.log(System.Logger.Level.ERROR,
                    "Unexpected error during phase execution. Phase: {0}. Error: {1}", phaseNum, e.getMessage(), e);
        } finally {
            notifyPhaseCompleted(phaseNum);
        }
    }

    /**
     * Проверяет, что фабрика анимации корректно задана.
     *
     * @param animationFactory Экземпляр фабрики.
     */
    private void validateAnimationFactory(AnimationFactory animationFactory) {
        if (animationFactory == null) {
            throw new IllegalStateException("AnimationFactory is not set. Ensure it is properly initialized.");
        }
        this.animationFactory = animationFactory;
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
                                "Error executing frame: {0}. Error: {1}", animationFrame.getClass().getSimpleName(), e.getMessage(), e);
                        throw e;
                    }
                })
                .toList();
    }

    /**
     * Выполняет список задач с учетом времени выполнения.
     *
     * @param tasks        Список задач.
     * @param phaseDuration Длительность этапа.
     * @param phaseNum     Номер текущего этапа.
     */
    private void executeTasksWithTimeout(List<Callable<Void>> tasks, long phaseDuration, int phaseNum) throws Exception {
        animationFactory.getTaskExecutor().executeTasksWithTimeout(tasks, phaseDuration, e ->
                AnimationFactory.logger.log(System.Logger.Level.ERROR,
                        "Task failed during phase {0}. Error: {1}", phaseNum, e.getMessage())
        );
    }

    /**
     * Уведомляет фабрику о завершении текущего этапа.
     *
     * @param phaseNum Номер текущего этапа.
     */
    private void notifyPhaseCompleted(int phaseNum) {
        AnimationFactory.logger.log(System.Logger.Level.INFO,
                "Notifying factory about phase {0} completion.", phaseNum);
        animationFactory.onPhaseCompleted(animationFactory.getCurrentPhase());
    }

    /**
     * Устанавливает текущую фабрику анимации.
     *
     * @param animationFactory Экземпляр фабрики анимации.
     */
    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
