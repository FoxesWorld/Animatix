package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import java.util.List;
import java.util.concurrent.Callable;

public class AnimationPhaseExecutor {
    private AnimationFactory animationFactory;

    public AnimationPhaseExecutor() {
    }

    public void executePhase(AnimationFactory animationFactory, List<AnimationFrame> animationFrames, int phaseNum) {
        AnimationFactory.logger.log(System.Logger.Level.INFO, "Executing phase: {0}", phaseNum);

        // Список задач для выполнения
        List<Callable<Void>> tasks = animationFrames.stream()
                .<Callable<Void>>map(animationFrame -> () -> {
                    try {
                        animationFrame.run();
                        return null;
                    } catch (Exception e) {
                        AnimationFactory.logger.log(System.Logger.Level.ERROR,
                                "Error executing frame: {0}", animationFrame.getClass().getSimpleName(), e);
                        throw e;
                    }
                })
                .toList();

        try {
            // Выполнение задач с таймаутом
            long phaseDuration = animationFactory.getCurrentPhase().getDuration();
            animationFactory.getTaskExecutor().executeTasksWithTimeout(
                    tasks,
                    phaseDuration,
                    e -> AnimationFactory.logger.log(System.Logger.Level.ERROR,
                            "Task failed during phase {0}: {1}", phaseNum, e.getMessage())
            );
            AnimationFactory.logger.log(System.Logger.Level.INFO, "Phase {0} completed successfully", phaseNum);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AnimationFactory.logger.log(System.Logger.Level.WARNING,
                    "Phase execution interrupted: {0}", phaseNum);

        } catch (Exception e) {
            AnimationFactory.logger.log(System.Logger.Level.ERROR,
                    "Unexpected error during phase execution: {0}", e.getMessage(), e);
        } finally {
            notifyPhaseCompleted();
        }
    }


    private void notifyPhaseCompleted() {
        AnimationFactory.logger.log(System.Logger.Level.DEBUG, "Notifying factory about phase completion");
        animationFactory.onPhaseCompleted(animationFactory.getCurrentPhase());
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
