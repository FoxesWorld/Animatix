package org.foxesworld.animatix.animation;

import java.util.Collection;
import java.util.concurrent.*;

import org.foxesworld.animatix.AnimationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static TaskExecutor instance;

    private final ExecutorService executorService;

    public TaskExecutor(AnimationFactory animationFactory) {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        logger.info("TaskExecutor initialized with thread pool size: {}", THREAD_POOL_SIZE);
    }


    /**
     * Выполняет список задач параллельно с ожиданием завершения.
     *
     * @param tasks  Список задач для выполнения.
     * @param timeout Тайм-аут в миллисекундах.
     * @throws InterruptedException Если выполнение прервано.
     */
    public void executeTasksWithTimeout(Iterable<Runnable> tasks, long timeout) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(((Collection<?>) tasks).size());

        for (Runnable task : tasks) {
            executorService.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    logger.error("Task execution failed", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        if (!latch.await(timeout, TimeUnit.MILLISECONDS)) {
            logger.warn("Tasks did not complete within the timeout of {} ms", timeout);
        } else {
            logger.info("All tasks completed successfully");
        }
    }

    /**
     * Выполняет одиночную задачу с таймаутом.
     *
     * @param task Задача для выполнения.
     * @param timeout Тайм-аут в миллисекундах.
     * @throws TimeoutException Если задача не завершилась вовремя.
     * @throws InterruptedException Если выполнение прервано.
     */
    public void executeTaskWithTimeout(Runnable task, long timeout) throws TimeoutException, InterruptedException {
        Future<?> future = executorService.submit(task);
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
            logger.info("Task completed successfully within timeout: {} ms", timeout);
        } catch (TimeoutException e) {
            logger.error("Task execution timed out after {} ms", timeout);
            throw e;
        } catch (Exception e) {
            logger.error("Error during task execution", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Запускает задачу асинхронно.
     *
     * @param task Задача для выполнения.
     */
    public void submitTask(Runnable task) {
        executorService.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Async task execution failed", e);
            }
        });
    }

    /**
     * Завершает работу TaskExecutor и освобождает ресурсы.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                logger.warn("Executor forcefully shutdown due to timeout");
            } else {
                logger.info("Executor shut down gracefully");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            logger.error("Shutdown interrupted", e);
        }
    }
}
