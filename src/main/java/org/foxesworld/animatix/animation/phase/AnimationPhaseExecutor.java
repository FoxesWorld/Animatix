package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.config.KeyFrame;

import java.util.List;
import java.util.concurrent.Callable;

public class AnimationPhaseExecutor {

    private final AnimationFactory animationFactory;

    public AnimationPhaseExecutor(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    /**
     * Executes the specified animation phase.
     *
     * @param animationFrames List of animation frames to execute.
     * @param phase           Current animation phase.
     * @param phaseNum        Phase number.
     */
    public void executePhase(KeyFrame keyFrame, List<AnimationFrame> animationFrames, AnimationPhase phase, int phaseNum) {
        long phaseDuration = keyFrame.getDuration();
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
            notifyPhaseCompleted(phase, phaseNum);
        }
    }

    /**
     * Prepares a list of tasks to execute.
     *
     * @param animationFrames List of animation frames.
     * @return List of tasks as Callable.
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
     * Executes tasks with a specified timeout.
     *
     * @param tasks         List of tasks.
     * @param phaseDuration Phase duration.
     * @param phaseNum      Phase number.
     * @throws Exception If task execution fails.
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
     * Notifies the factory about the completion of the current phase.
     *
     * @param phase     Current animation phase.
     * @param phaseNum  Phase number.
     */
    private void notifyPhaseCompleted(AnimationPhase phase, int phaseNum) {
        AnimationFactory.logger.log(System.Logger.Level.INFO,
                "Notifying factory about phase {0} completion.", phaseNum);
        animationFactory.onPhaseCompleted(phase);
    }
}
